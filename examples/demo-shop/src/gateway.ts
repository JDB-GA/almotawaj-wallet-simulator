import { API_KEY_HEADER, config } from './config.ts'
import type { CheckoutSession, Problem } from './types.ts'

const SESSIONS_URL = `${config.apiUrl}/api/gateway/checkout-sessions`

export class GatewayError extends Error {
  readonly problem: Problem

  constructor(problem: Problem) {
    super(problem.detail)
    this.problem = problem
  }
}

const toProblem = async (response: Response): Promise<Problem> => {
  const body = (await response.json().catch(() => ({}))) as Partial<Problem>
  return {
    status: response.status,
    code: body.code ?? (response.status === 401 ? 'UNAUTHORIZED' : 'UNKNOWN'),
    detail: body.detail ?? 'The API key is missing, wrong or revoked.',
    errors: body.errors,
  }
}

const call = async (url: string, method: 'GET' | 'POST', body?: unknown): Promise<CheckoutSession> => {
  const response = await fetch(url, {
    method,
    headers: { [API_KEY_HEADER]: config.apiKey, 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  })
  if (!response.ok) {
    throw new GatewayError(await toProblem(response))
  }
  return (await response.json()) as CheckoutSession
}

interface CreateSessionInput {
  orderReference: string
  amount: number
  description: string
  returnUrl: string
  expiresInMinutes?: number
}

export const gateway = {
  createSession: (input: CreateSessionInput) => call(SESSIONS_URL, 'POST', input),
  getSession: (sessionId: string) => call(`${SESSIONS_URL}/${encodeURIComponent(sessionId)}`, 'GET'),
  cancelSession: (sessionId: string) => call(`${SESSIONS_URL}/${encodeURIComponent(sessionId)}/cancel`, 'POST'),
  refundSession: (sessionId: string) => call(`${SESSIONS_URL}/${encodeURIComponent(sessionId)}/refund`, 'POST'),
}
