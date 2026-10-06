import { env } from '@/config/env'
import { GATEWAY_PATH, GUIDE_HEADER, SAMPLE_API_KEY, SAMPLE_SESSION_ID } from '../constants/developers.constants'

const SAMPLE_BODY = { orderReference: 'ORDER-1042', amount: 12.5, description: '2 x Arabic coffee beans' }

const buildCodeSamples = (apiUrl: string, appUrl: string) => {
  const sessionsUrl = `${apiUrl}${GATEWAY_PATH}`
  const sessionUrl = `${sessionsUrl}/${SAMPLE_SESSION_ID}`
  const session = {
    id: SAMPLE_SESSION_ID,
    ...SAMPLE_BODY,
    status: 'PENDING',
    payerName: null,
    checkoutUrl: `${appUrl}/checkout/${SAMPLE_SESSION_ID}`,
    expiresAt: '2026-10-06T17:12:08Z',
    paidAt: null,
    refundedAt: null,
    createdAt: '2026-10-06T16:42:08Z',
  }

  return {
    createCurl: [
      `curl -X POST ${sessionsUrl} \\`,
      `  -H "${GUIDE_HEADER}: ${SAMPLE_API_KEY}" \\`,
      '  -H "Content-Type: application/json" \\',
      `  -d '${JSON.stringify(SAMPLE_BODY)}'`,
    ].join('\n'),
    createNode: [
      `const response = await fetch('${sessionsUrl}', {`,
      "  method: 'POST',",
      '  headers: {',
      `    '${GUIDE_HEADER}': process.env.WALLET_API_KEY,`,
      "    'Content-Type': 'application/json',",
      '  },',
      `  body: JSON.stringify(${JSON.stringify(SAMPLE_BODY)}),`,
      '})',
      '',
      'if (!response.ok) {',
      '  const problem = await response.json()',
      '  throw new Error(problem.code)',
      '}',
      '',
      'const session = await response.json()',
      'redirect(session.checkoutUrl)',
    ].join('\n'),
    createResponse: JSON.stringify(session, null, 2),
    confirmCurl: `curl ${sessionUrl} \\\n  -H "${GUIDE_HEADER}: ${SAMPLE_API_KEY}"`,
    confirmResponse: JSON.stringify({ ...session, status: 'PAID', payerName: 'Sara A.', paidAt: '2026-10-06T16:45:31Z' }, null, 2),
    cancelCurl: `curl -X POST ${sessionUrl}/cancel \\\n  -H "${GUIDE_HEADER}: ${SAMPLE_API_KEY}"`,
    refundCurl: `curl -X POST ${sessionUrl}/refund \\\n  -H "${GUIDE_HEADER}: ${SAMPLE_API_KEY}"`,
    errorResponse: JSON.stringify(
      {
        title: 'Conflict',
        status: 409,
        detail: 'A payment already exists for this order reference',
        instance: GATEWAY_PATH,
        code: 'ORDER_ALREADY_EXISTS',
      },
      null,
      2,
    ),
  }
}

export const getCodeSamples = () => buildCodeSamples(env.apiUrl, window.location.origin)
