# Business Rules: Phase 03

[← Back to the README](../../README.md)

These extend the [Phase 02 rules](../phase-02/business-rules.md).

30. A voucher has a price, a face value, a quantity, a per-client limit, a sale end date and an optional expiry date. Only an `ACTIVE` voucher whose sale has not ended and that has stock left can be bought. The expiry date, if set, cannot be before the sale end date.
31. Buying a voucher takes one unit of stock with a single database update that only succeeds while stock is above zero, and debits the buyer's locked wallet in the same transaction. If the stock update fails the buyer gets `409 VOUCHER_SOLD_OUT` and nothing is charged. A database check also keeps stock at zero or above.
32. Only KYC-approved clients buy vouchers, and never more than the voucher's per-client limit. Each purchase gets a unique code. The money goes to the merchant's wallet.
33. A voucher carries a version number; an edit made from an out-of-date copy is rejected (`409`) instead of overwriting a newer change.
34. A payout batch holds 1–100 items for active wallet holders, each 0.100–5,000.000 BHD. It returns `202` and runs in the background on a fixed pool of 5 threads.
35. Every payout item locks the merchant's wallet before debiting it, so the balance never goes below zero. Items that don't fit are marked `FAILED` with the reason; the others are `PAID`. The batch ends `COMPLETED` or `PARTIALLY_FAILED`, and progress is pushed live.
36. Voucher purchases accept an `Idempotency-Key` header (at most 64 characters). The key is unique per user; repeating it returns the first purchase and moves no money again, even when both requests arrive at the same moment.
37. Seeding gives the demo merchant four vouchers, including one with a single unit in stock for the race demonstration.
38. Write endpoints are rate-limited per signed-in user and endpoint (per address for anonymous requests), so buyers behind the same network do not block each other during a sale.
39. The Concurrency Lab (super admin only) runs races in throw-away sandbox accounts that are deleted after every run; real accounts are never touched.
40. A bought voucher is used once, only when paying a checkout of the merchant that sold it. It is marked used with a single database update that only succeeds while it is unused, in the same transaction as the payment, so two tabs paying with the same voucher never both succeed. Its face value comes off the amount (any leftover is lost) and the client pays the rest. A voucher past its expiry date cannot be used. A refund returns only the cash part and makes the voucher usable again.
