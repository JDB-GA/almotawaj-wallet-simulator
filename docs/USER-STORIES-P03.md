# User Stories – P03

Format: _As a [user], I want to [action], so that [reason]._ These extend the [P02 stories](USER-STORIES-P02.md) and are planned for Project 3; the checkboxes are the acceptance criteria.

## Epic: Statements

**US-36 – Bank-style statement.** As a wallet holder, I want my statement to look like a bank statement, so that I can hand it to anyone who asks for proof of my transactions.

- [ ] Letterhead and "Page X of Y" on every page, statement number, issue date and period.
- [ ] Opening balance, total credits, total debits and closing balance; opening + credits − debits = closing.
- [ ] Separate debit and credit columns, a running balance and every transaction in the period, in English or Arabic.

## Epic: Vouchers

**US-37 – Sell vouchers.** As a merchant, I want to sell a limited number of vouchers, so that I can run promotions.

- [ ] I set a title, price, face value, quantity, a per-client limit and an expiry date.
- [ ] I can pause or edit a voucher; if someone changed it after I opened it, my edit is rejected instead of overwriting theirs.

**US-38 – Buy a voucher.** As a client, I want to buy a voucher with my wallet balance, so that I get it instantly.

- [ ] I need KYC approval and enough balance; I receive a unique voucher code.
- [ ] I can see the vouchers I bought.

**US-39 – No overselling.** As a client, I want the system to stop two people from buying the last voucher, so that nobody pays for something that doesn't exist.

- [ ] When several clients buy the last voucher at the same moment, exactly one succeeds (`201`) and the others get `409 VOUCHER_SOLD_OUT`.
- [ ] Stock never goes below zero and nobody is charged without receiving a voucher.

## Epic: Bulk payouts

**US-40 – Pay many people at once.** As a merchant, I want to pay a list of recipients in one request, so that payroll and payouts are fast.

- [ ] The request returns `202 Accepted` right away; the payouts run in the background.
- [ ] I see live progress and a final result for each recipient.

**US-41 – Never overdraw.** As a merchant, I want my balance never to go negative when many payouts run at the same time, so that I never pay money I don't have.

- [ ] Items that don't fit in my balance are marked failed with the reason; the rest succeed.

## Epic: Reliability

**US-42 – Charged once.** As a wallet holder, I want a double click or a retried request to charge me only once, so that I never pay twice by accident.

- [ ] Transfers and voucher purchases accept an `Idempotency-Key`; repeating it returns the first result.

