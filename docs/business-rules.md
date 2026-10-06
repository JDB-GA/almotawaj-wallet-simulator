# Roles, permissions and business rules

[← Back to the README](../README.md)

## Roles and permissions

| Role          | Can do                                                               |
| ------------- | -------------------------------------------------------------------- |
| `CLIENT`      | Register, verify email, submit and track KYC, manage their password; after KYC approval: wallet, receive transfers, send and request money, pay merchants |
| `MERCHANT`    | Wallet, receive transfers, send and request money, API keys, payment links, cancel and refund payments |
| `ADMIN`       | Only what their permissions allow                                    |
| `SUPER_ADMIN` | Everything, including granting permissions and reading the audit log |

| Permission    | Grantable to | Unlocks                                                        |
| ------------- | ------------ | -------------------------------------------------------------- |
| `KYC_REVIEW`  | `ADMIN`      | KYC review list, documents, approve/reject                     |
| `USER_MANAGE` | `ADMIN`      | User search, creation, contact edits, suspend/reactivate/close, a user's transactions |
| `STATISTICS_VIEW` | `ADMIN`  | User counts per role, transaction totals and every wallet's transactions |

Admins with `USER_MANAGE` manage clients and merchants only; only the super admin manages admins. Nobody can manage their own account or a super admin from user management.

## Business rules

1. Unverified emails can only reach verification and account endpoints.
2. One-time codes expire after 10 minutes (invitations: 48 hours), allow 5 attempts and can be resent once every 60 seconds.
3. A client can have only one pending KYC application and cannot resubmit after approval.
4. A CPR number can belong to only one pending or approved application.
5. KYC applicants must be 18+ and both identity documents must be unexpired.
6. Identity documents must be real PDFs and photos real JPEG/PNG files (checked by content), 5 MB max each.
7. Only `PENDING` applications can be reviewed; rejections require a reason. Two reviewers cannot decide the same application at once (row lock).
8. User status transitions: `ACTIVE ⇄ SUSPENDED`, any non-closed status → `CLOSED`. Closed accounts are kept but can never sign in or be changed.
9. Suspending or closing a user, or changing/resetting a password, takes effect on the very next request.
10. Permissions are only grantable to the roles that may hold them; super admin permissions are fixed.
11. Forgot-password responses never reveal whether an email exists.
12. Only KYC-approved clients and merchants have wallets; each wallet has exactly one IBAN that never changes, and each user has one wallet.
13. Limits live in `WalletLimits`: top-ups and transfers are 0.100–5,000.000 BHD each, and at most 10,000.000 BHD may be received by top-up and 10,000.000 BHD sent by transfer per day (Bahrain time). Checkout payments will have their own daily limit.
14. Transfers need enough balance, cannot go to yourself, and only reach active, eligible wallet holders.
15. Money movements lock the affected wallet rows (both, in a fixed order, for transfers), so concurrent requests can never overspend or exceed a daily limit.
16. Recipient suggestions need at least 3 characters, return at most 5 masked results and never include yourself.
17. A payment request can only be paid or declined by the payer and cancelled by the requester, and only while it is `PENDING`. Paying it is a normal transfer, so the balance and daily transfer limit apply; the request row is locked while it is decided, so it cannot be paid twice.
18. Every sign-in, sign-out, registration, email verification, password change, KYC decision, permission change, user change, top-up, transfer, payment request, API key and merchant payment action is written to the audit log with the actor, the target and the time.
19. Statistics count a transfer once on each side: the sender's debit is part of "money out" and the receiver's credit is part of "money in".
20. A merchant can have at most 5 active API keys. A key is shown once, stored only as a hash, works only on the gateway endpoints and stops working as soon as it is revoked or the merchant is suspended or closed.
21. An order reference can be used for one checkout session per merchant, so an order can never be paid twice. A session can only be paid while it is `PENDING` and not older than 30 minutes; the session row is locked while it is paid, cancelled or refunded.
22. Only KYC-approved clients pay checkouts. A payment is 0.100–5,000.000 BHD, needs enough balance and counts towards a separate daily payment limit of 10,000.000 BHD.
23. Only a `PAID` session can be refunded, once, in full, and only if the merchant's balance covers it.
24. Sessions that are not paid in time are marked `EXPIRED` by a job that runs every minute.
25. A wallet holder can download a receipt only for a transaction of their own wallet. A statement covers only their own wallet and lists at most the 500 newest matching transactions. Both downloads are written to the audit log.
26. A client's name is their verified KYC name and cannot be edited. A merchant sets a business name (2–70 characters), which replaces their email wherever their name is shown to other users. Profile pictures must be real JPEG or PNG files up to 5 MB; uploading a new one deletes the old file.
27. A checkout session may carry a return address; after paying, the customer is sent there with the session id and order reference. The return is a convenience only, so merchants must confirm the status through the API or the callback.
28. A merchant has at most one callback URL. Every status change of their sessions is posted to it, signed with a secret that only that merchant can read. Callback URLs must be public https addresses.
29. Gateway calls are limited to 120 per minute for each merchant account, whatever address they come from.
