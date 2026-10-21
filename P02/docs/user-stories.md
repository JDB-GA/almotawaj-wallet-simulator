# User Stories

Format: _As a [user], I want to [action], so that [reason]._ Every story below is built; the checkboxes are its acceptance criteria.

## Epic: Accounts

**US-01 – Register.** As a visitor, I want to create an account with my email, mobile and password, so that I can use the wallet.

- [ ] Email and 8-digit Bahraini mobile must be unique; password 8–72 characters.
- [ ] A 6-digit verification code is emailed in my language.
- [ ] I am signed in right after registering.

**US-02 – Verify email.** As a new client, I want to confirm my email with a code, so that my account is trusted.

- [ ] Codes expire after 10 minutes and allow 5 attempts.
- [ ] I can resend a code once every 60 seconds.
- [ ] Until verified I can only reach the verification page.

**US-03 – Log in.** As a user, I want to sign in with my email or mobile number, so that I can reach my account from any device.

- [ ] Wrong credentials and suspended/closed accounts are rejected with the same message.
- [ ] The session is an httpOnly cookie; I land on the page for my role.

**US-04 – Forgot password.** As a user who forgot my password, I want to reset it with a code sent to my email, so that I can get back into my account.

- [ ] The response never reveals whether the email exists.
- [ ] Resetting signs me out on every other device.

**US-05 – Change password.** As a signed-in user, I want to change my password, so that I can keep my account secure.

- [ ] The current password is required; the new one must be different.
- [ ] Other devices are signed out; I stay signed in.

## Epic: Identity verification (KYC)

**US-06 – Submit KYC.** As a client, I want to submit my personal details, address, CPR, passport and photo, so that my identity can be verified.

- [ ] CPR and passport must be PDFs, the photo JPEG/PNG, each up to 5 MB, checked by real content.
- [ ] I must be 18+ and both documents unexpired.
- [ ] A CPR can belong to only one pending or approved application.

**US-07 – Track my verification.** As a client, I want to see my application status and documents, so that I know where my verification stands.

- [ ] I see Under review / Verified / Rejected with the reason.
- [ ] I can preview and download my own documents only.
- [ ] After a rejection the form is pre-filled so I only fix what was wrong.

**US-08 – Get notified of the decision.** As a client, I want an email when my identity is approved or rejected, so that I don't have to keep checking.

- [ ] The email is in English and Arabic and includes the rejection reason.

## Epic: KYC review

**US-09 – Review queue.** As a reviewer, I want to list applications filtered by status, so that I can work through pending requests.

- [ ] Requires the `KYC_REVIEW` permission; paged, newest first.

**US-10 – Decide an application.** As a reviewer, I want to inspect documents and approve or reject with a reason, so that only real identities are verified.

- [ ] Only pending applications can be decided; a reason is required to reject.
- [ ] Two reviewers cannot decide the same application.
- [ ] My name and the time are recorded on the decision.

## Epic: User management

**US-11 – Find users.** As an admin with `USER_MANAGE`, I want to search users by email, mobile or name and filter by role and status, so that I can find any account quickly.

**US-12 – Create users.** As an admin, I want to create users who then set their own password from an invitation email, so that I never handle their passwords.

- [ ] Admins create clients and merchants; only the super admin creates admins and sets permissions.
- [ ] The invitation code is valid for 48 hours.

**US-13 – Edit contact details.** As an admin, I want to correct a user's email or mobile, so that their account stays reachable.

- [ ] Duplicates are rejected; a changed email must be verified again.

**US-14 – Suspend, reactivate and close.** As an admin, I want to suspend, reactivate or close (soft delete) accounts, so that I can stop misuse without losing data.

- [ ] Changes take effect on the user's next request.
- [ ] Closed accounts stay in the database and cannot be reopened.
- [ ] I cannot manage myself, a super admin, or (unless super admin) another admin.

**US-15 – Manage permissions.** As the super admin, I want to grant and revoke permissions with checkboxes, so that each admin can do exactly their job.

- [ ] Permissions can only be given to roles allowed to hold them.

## Epic: Administration and operations

**US-16 – Audit trail (API).** As the super admin, I want every administrative, security and money action recorded, so that I can see who did what and when.

- [ ] KYC decisions, permission changes, user creation, edits, status changes and password changes are stored.
- [ ] Registration, sign-in, sign-out, email verification, top-ups, transfers and payment requests are stored.

**US-17 – Audit log screen.** As the super admin, I want to browse and filter the audit log in the app, so that I don't need direct API access.

- [ ] Actions, targets and details are shown in English and Arabic.

**US-30 – Statistics.** As an admin with `STATISTICS_VIEW`, I want to see how many users of each type exist and how much money moves, so that I can follow the system at a glance.

- [ ] Total users and users per role (clients, merchants, admins, super admins).
- [ ] Number of transactions, money in and money out for the current filters.
- [ ] Every wallet's transactions with the owner's email, searchable by that email.

**US-31 – A user's transactions.** As an admin with `USER_MANAGE`, I want to open a user's transactions from their profile, so that I can answer their questions.

- [ ] Same search and filters as the wallet history; the details panel shows the user as the wallet owner.

**US-18 – Seed demo data.** As a developer or grader, I want to load demo accounts with one request, so that I can try every role immediately.

- [ ] Protected by a seed token; safe to run repeatedly; disabled when no token is configured.

## Epic: Wallet

**US-24 – My wallet.** As a verified client or a merchant, I want a wallet with my own IBAN and balance, so that I can receive and spend money.

- [ ] The wallet opens automatically; clients need approved KYC.
- [ ] The IBAN is a valid, unique Bahraini IBAN that never changes; balances use 3 decimals (BHD).

**US-25 – Receive a bank transfer.** As a wallet holder, I want to receive money from an account at another bank, so that I can fund my wallet.

- [ ] I only choose a demo source account and an amount; the sender details are filled in by the server.
- [ ] 0.100–5,000.000 BHD per transfer and at most 10,000.000 BHD per day.

**US-26 – Send money.** As a wallet holder, I want to send money to another user by email, mobile number or IBAN, so that I can pay friends and businesses.

- [ ] Typing 3+ characters suggests up to 5 masked matches; I see a masked preview before sending.
- [ ] I need enough balance, cannot send to myself, and can send at most 10,000.000 BHD per day.
- [ ] Concurrent transfers can never overspend my balance.

**US-27 – Transaction history.** As a wallet holder, I want to see my transactions with the counterparty and reference, so that I can track my money.

- [ ] Paged, newest first, credits and debits clearly marked.

**US-28 – Search transactions.** As a wallet holder, I want to search and filter my transactions, so that I can find a payment quickly.

- [ ] Search by counterparty name, reference or note (case-insensitive).
- [ ] Filter by type, money in/out and date range; filters stay in the URL.

**US-33 – Receipts and statements.** As a wallet holder, I want to download a PDF receipt for a transaction and a PDF statement of my history, so that I can keep or share a record.

- [ ] The receipt shows the amount, type, reference, date, both sides and the note.
- [ ] The statement follows my current filters and shows money in and money out totals.
- [ ] Both are available in English and Arabic (right-to-left).

**US-29 – Request money.** As a wallet holder, I want to request money from another user, so that they can pay me with one tap.

- [ ] The payer can pay or decline; I can cancel while it is pending.
- [ ] Paying is a normal transfer: balance and daily limit apply, and a request can never be paid twice.

## Epic: Profile

**US-19 – Profile.** As a client or merchant, I want a profile page with my details and a picture, so that my account feels personal.

- [ ] I see my name, email, mobile number, verification status and join date.
- [ ] I can upload, change and remove a picture: JPEG/PNG only, up to 5 MB, checked by content.
- [ ] As a merchant I can set a business name that customers see instead of my email.

## Epic: Payments

**US-20 – Merchant API keys.** As a merchant, I want an API key for my store, so that my website can create payments.

- [ ] The key is shown once, stored as a hash and can be revoked at any time; at most 5 active keys.
- [ ] A key only works on the gateway endpoints.

**US-21 – Checkout session.** As a merchant, I want to create a checkout session for an order, so that my customer can pay with the wallet.

- [ ] Statuses: `PENDING → PAID / CANCELLED / EXPIRED`, `PAID → REFUNDED`.
- [ ] One order reference can only be paid once (double-payment prevention).
- [ ] Sessions expire after 30 minutes; I can create them from my server or as a payment link in the dashboard.

**US-22 – Pay a checkout.** As a client, I want to pay a merchant's checkout from my wallet, so that I can buy online.

- [ ] Only verified (KYC approved) clients can pay.
- [ ] I see the merchant, the order and the amount and confirm before paying; if I am signed out I return to the payment after signing in.
- [ ] I need enough balance and can pay at most 10,000.000 BHD per day.

**US-34 – Return and callback.** As a merchant, I want my customer sent back to my site after paying and my server told about the payment, so that the order completes without anyone refreshing a page.

- [ ] A session can carry a return address; the checkout page redirects there after payment.
- [ ] I set one callback URL; each status change is posted to it with a signature I can verify.
- [ ] Callbacks are retried up to 3 times and can only go to public https addresses.

**US-35 – Developer guide.** As a merchant's developer, I want a guide with real request and response examples, so that I can integrate without asking for help.

**US-32 – Manage payments.** As a merchant, I want to see my payments and refund or cancel them, so that I can run my store from the dashboard.

- [ ] Filter by status; see who paid (masked) and when.
- [ ] Refunding sends the full amount back to the customer once.

## Epic: Notifications

**US-23 – Live updates.** As a wallet holder, I want to see money received and payment requests instantly, so that I don't need to refresh.

- [ ] Delivered with Server-Sent Events after the transaction commits, with a toast and a sound.
