# Roles and permissions

[← Back to the README](../../README.md)

## Roles and permissions

| Role          | Can do                                                                                                                                                    |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `CLIENT`      | Register, verify email, submit and track KYC, manage their password; after KYC approval: wallet, receive transfers, send and request money, pay merchants |
| `MERCHANT`    | Wallet, receive transfers, send and request money, API keys, payment links, cancel and refund payments                                                    |
| `ADMIN`       | Only what their permissions allow                                                                                                                         |
| `SUPER_ADMIN` | Everything, including granting permissions and reading the audit log                                                                                      |

| Permission        | Grantable to | Unlocks                                                                               |
| ----------------- | ------------ | ------------------------------------------------------------------------------------- |
| `KYC_REVIEW`      | `ADMIN`      | KYC review list, documents, approve/reject                                            |
| `USER_MANAGE`     | `ADMIN`      | User search, creation, contact edits, suspend/reactivate/close, a user's transactions |
| `STATISTICS_VIEW` | `ADMIN`      | User counts per role, transaction totals and every wallet's transactions              |

Admins with `USER_MANAGE` manage clients and merchants only; only the super admin manages admins. Nobody can manage their own account or a super admin from user management.
