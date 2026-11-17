# Almotawaj Wallet: User Guide

This guide explains how to use the Almotawaj Digital Wallet. Amounts are in Bahraini Dinar (BHD).

## Account

### Create an account
1. Open the wallet and choose **Register**.
2. Enter your name, email, mobile number and a password of at least 8 characters.
3. Check your email for the verification code and enter it.
4. Your wallet is created automatically after you register.

### Log in
- Log in with your email or your mobile number and your password.

### Forgot your password
1. On the login page choose **Forgot your password?**.
2. Enter your email. If an account exists, a code is sent to it.
3. Enter the code and choose a new password.

### Change your password
- Open **Account → Change password**.
- You stay signed in on this device; other devices are signed out.

### Profile
- Open **Account → Profile** to see your details.
- You can upload a profile picture (JPG or PNG, up to 5 MB) or remove it.
- Merchants can set a business name. Customers see it on payments and transfers instead of the email.

## Identity verification (KYC)

You must verify your identity to unlock all wallet features, such as sending money.

### How to verify
1. Open **Verification**.
2. Enter your personal information exactly as it appears on your CPR card.
3. Enter your current residential address in Bahrain.
4. Upload clear copies of your identity documents. They must be valid.
5. Submit the request.

### After you submit
- **Under review:** an admin is checking your documents. You are notified when the review is done.
- **Approved:** your identity is verified and you have full access.
- **Rejected:** read the reason, correct the issue and submit again.

## Wallet and balance

- The **Wallet** page shows your balance, your IBAN and your recent transactions.
- You can copy your IBAN to share it with people who want to send you money.

### Add money (top up)
- Use **Receive a bank transfer** to simulate an incoming transfer from one of your demo accounts at other banks.
- This is a simulation for the demo only. The money is added to your balance at once.
- Each transfer has a maximum amount, and there is a daily limit. The page shows how much is left today.

## Send money

1. Open **Send money**.
2. Type the recipient's email or mobile number, or paste their IBAN.
3. Enter the amount and an optional note.
4. Confirm. The money moves instantly.

Rules:
- You must be verified to send money.
- There is a maximum per transfer and a daily limit. The page shows your balance and what is left today.
- You cannot send more than your balance.

## Payment requests

Ask another wallet holder to send you money.

1. Open **Payment requests** and choose **Request money**.
2. Enter the person's email, mobile or IBAN, the amount and a note.
3. They see the request and can **pay** or **decline** it.
4. You can **cancel** a request you sent while it is still waiting.

## Transactions

- Open **Transactions** to see every transfer and payment.
- Search by name, reference or note.
- Filter by direction (money in or money out) and sort by date or amount.
- Open a transaction to see its details.
- Download a **receipt (PDF)** for one transaction, or a **statement (PDF)** for your history.

## Vouchers

### Buy a voucher
1. Open **Vouchers** to see the voucher shop.
2. Each voucher shows its price, face value, how many are left and the expiry date.
3. Choose **Buy**. The price is taken from your balance.
4. Your bought vouchers appear with their codes. Use a code when you pay the merchant that sold it.

Rules:
- Stock is limited. Each voucher goes to one buyer only, even when many people press Buy at the same moment.
- Some vouchers have a maximum number per person.

## For merchants

### Sell vouchers
1. Open **My vouchers** and create a voucher.
2. Set a title, the price, the face value, how many can be sold, and an optional expiry date.
3. Stock is protected, so you never sell more than you have.

### Accept payments (payment links)
1. Open **Payments** and create a payment for an order.
2. Enter the order reference, amount and description, and an optional return address.
3. Send the link to your customer. They pay it from their wallet.
- Links expire after 30 minutes.
- Each order reference can be used for one payment only.
- You can cancel a waiting payment, or refund a paid one. A refund sends the money back from your wallet.

### API keys
- Your server uses an API key to create payments.
- Create a key in **API keys** and give it a name. The full key is shown only once, so store it safely.
- You can revoke a key at any time. It stops working immediately.

### Payment callbacks (webhooks)
- Add a public https address. The wallet calls it when a payment is paid, cancelled, expired or refunded.
- Use the secret to check that a callback really came from the wallet.

### Bulk payouts
1. Open **Bulk payouts**.
2. Add recipients (email, mobile or IBAN) and amounts.
3. Payouts run in the background. Your balance can never go below zero.
4. The result shows how many were paid and how many failed, and why (for example, not enough balance).

## For admins

- **Dashboard:** statistics about users and transactions.
- **KYC reviews:** approve or reject identity verification requests.
- **Users:** create users, change contact details, grant permissions, suspend, reactivate or close accounts, and see a user's transactions.
- **Audit log (super admin):** every important action, such as logins, transfers and KYC decisions.
- **Concurrency Lab (super admin):** runs a safe simulation that shows how the wallet prevents double spending.
- **Assistant files (super admin):** upload the documents this assistant answers from.

## Assistant

- Click the round assistant button at the bottom of any page.
- Ask a question in English or Arabic. The answer comes from the wallet documentation, with the file names as sources.
- If the documentation does not cover your question, the assistant says it doesn't know.
