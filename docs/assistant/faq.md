# Almotawaj Digital Wallet: Frequently Asked Questions

## General

**What is Almotawaj Digital Wallet?**
A bilingual (English and Arabic) digital wallet for Bahrain. You can hold money, send and receive it, request payments, buy vouchers and pay merchants.

**Who built it?**
Muntadher Almutawaj.

**Where can I use it?**
At https://wallet.almotawaj.com in any modern browser, on desktop or phone.

**Which currency does it use?**
Bahraini Dinar (BHD), with 3 decimal places.

**Which languages are supported?**
English and Arabic. Use the language switcher to change; Arabic shows right to left.

**Is this real money?**
No. It is a simulator for demonstration. Top-ups are simulated bank transfers.

## Account

**How do I create an account?**
Choose Register, enter your name, email, mobile number and a password of at least 8 characters, then enter the code sent to your email.

**I did not get the verification code. What should I do?**
Check your spam folder. You can resend the code once every 60 seconds.

**How long is a code valid?**
10 minutes. Invitation codes are valid for 48 hours. You have 5 attempts.

**Can I log in with my mobile number?**
Yes. Use your email or your mobile number with your password.

**I forgot my password.**
On the login page choose "Forgot your password?", enter your email, then enter the code and choose a new password.

**How do I change my password?**
Open Account, then Change password. Other devices are signed out; you stay signed in on this one.

**How do I change my profile picture?**
Open Account, then Profile. Upload a JPG or PNG up to 5 MB, or remove the current picture.

## Identity verification (KYC)

**Why do I need to verify my identity?**
To unlock all wallet features, such as sending money, buying vouchers and paying merchants.

**What do I need for KYC?**
Your details as on your CPR card, your address in Bahrain and valid identity documents.

**Who can apply?**
People aged 18 or older with unexpired identity documents.

**Which files can I upload?**
PDF documents and JPEG or PNG photos, up to 5 MB each. Files are checked by their content.

**How long does the review take?**
An admin reviews your request. You get an email when a decision is made.

**My KYC was rejected. What now?**
Read the reason shown on the Verification page, fix the issue and submit again.

## Wallet and top up

**Do I get an IBAN?**
Yes. Your wallet is created automatically with its own IBAN after you register.

**How do I add money?**
Use "Receive a bank transfer" on the Wallet page. It simulates a transfer from your demo bank accounts.

**What are the top-up limits?**
Each top-up is 0.100 to 5,000.000 BHD, and at most 10,000.000 BHD per day (Bahrain time).

## Sending and requesting money

**How do I send money?**
Open Send money, type the recipient's email or mobile number or paste their IBAN, enter the amount and confirm.

**What are the transfer limits?**
Each transfer is 0.100 to 5,000.000 BHD, and at most 10,000.000 BHD sent per day (Bahrain time). You cannot send more than your balance.

**Is the transfer instant?**
Yes. The money moves at once.

**Why can't I send money?**
You must be KYC-approved, have enough balance and stay within the daily limit.

**How do I ask someone to pay me?**
Open Payment requests, choose Request money, enter their email, mobile or IBAN, the amount and a note.

**Can I cancel a payment request?**
Yes, while it is still pending. The other person can pay or decline it.

**Can a payment request be paid twice?**
No. The request is locked while it is decided.

## Transactions and documents

**Where do I see my history?**
On the Transactions page. Search by name, reference or note, filter by direction and sort by date or amount.

**Can I get a receipt?**
Yes. Open a transaction and download the receipt PDF.

**Can I get a statement?**
Yes. Download the statement PDF from the Transactions page.

## Vouchers

**What is a voucher?**
A merchant sells it at a price, and it is worth a face value when you pay that merchant.

**How do I buy a voucher?**
Open Vouchers, choose one and press Buy. The price is taken from your balance.

**Where is my voucher code?**
Your bought vouchers appear with their codes. Use the code when you pay the merchant that sold it.

**What if many people buy the last voucher at once?**
Only one buyer gets it. The others see "sold out" and are not charged.

**Is there a limit per person?**
Some vouchers have a maximum number per person.

## For merchants

**How do I accept payments?**
Create a payment link on the Payments page and send it to your customer, or create payments from your server with an API key.

**How long does a payment link work?**
30 minutes. After that it expires.

**Can one order be paid twice?**
No. Each order reference can be paid only once.

**Can I refund a payment?**
Yes. A refund sends the money back to the customer from your wallet.

**What is an API key?**
A secret your server uses to create payments. It is shown only once when you create it. You can revoke it at any time.

**Is there an API rate limit?**
Yes. 120 calls per minute per merchant.

**What is a payment callback (webhook)?**
A public https address on your server. The wallet calls it when a payment is paid, cancelled, expired or refunded. Use the secret to check the call is real.

**How do I sell vouchers?**
Open My vouchers and create one with a title, price, face value, quantity and optional expiry date. Stock never oversells.

**How do bulk payouts work?**
Add up to 100 recipients and amounts (0.100 to 5,000.000 BHD each). Payouts run in the background, and your balance never goes below zero. The result shows how many were paid and how many failed, with the reason.

## Admins

**What can admins do?**
Only what their permissions allow: KYC review, user management or statistics.

**What can the super admin do?**
Everything, including granting permissions, reading the audit log, running the Concurrency Lab and managing the assistant files.

**What is the audit log?**
A record of every important action, such as logins, transfers and KYC decisions. Only the super admin can read it.

**What is the Concurrency Lab?**
A safe demo that races many buyers at the same time in a throw-away sandbox, to show how the wallet prevents double spending.

## Assistant (chatbot)

**What is the assistant?**
A chatbot that answers questions about the wallet from its documentation. Open it with the round button at the bottom of any page.

**How does it work?**
It uses RAG (Retrieval-Augmented Generation). It searches the uploaded documents with Gemini File Search, then answers from the matching parts and shows the file names as sources.

**Which languages does it answer in?**
The same language you ask in: English or Arabic.

**Why did it say "I don't know"?**
The documents do not cover your question. It only answers from the documentation.

**Who adds documents?**
The super admin, on the Assistant files page. Only .md and .txt files up to 5 MB are allowed.

**Do I need to be signed in?**
Yes. Only signed-in users can use the assistant.

## Security and privacy

**Is my data safe?**
The database, Kafka and KYC files are private on AWS. Secrets are stored in AWS Parameter Store, and no access keys are stored on servers.

**How am I kept signed in?**
With a secure login token (JWT) stored in a cookie.

**Can two actions at once break my balance?**
No. Money movements lock your wallet, so your balance can never go negative or exceed the limits.

## Technology

**How is the project built?**
6 Java Spring Boot microservices behind an API gateway, a React frontend, PostgreSQL, Kafka and Docker, deployed on AWS through Jenkins.

**Where is it hosted?**
AWS region eu-north-1 (Stockholm): Elastic Beanstalk, RDS, S3, CloudFront and EC2.

**How are updates deployed?**
Jenkins checks GitHub every 2 minutes, runs the tests, builds Docker images, pushes them to Amazon ECR and deploys to Beanstalk. If any test fails, nothing is deployed.

**How many tests are there?**
173 automated tests, plus Cucumber scenarios and a concurrency test where 20 buyers race for 1 voucher.
