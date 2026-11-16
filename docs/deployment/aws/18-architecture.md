# Step 18: AWS Architecture Diagram

[← Back to AWS](index.md)

**Goal:** one picture of the whole AWS setup for the README and the presentation.

**When:** after Steps 12 to 17, so the diagram shows the final setup.

## Plan

1. Draw the final setup (draw.io or the AWS architecture icons).
2. Show every resource with its real name:
   - Jenkins (developer Mac) → ECR, S3 versions
   - CloudFront (HTTPS) → S3 frontend and Elastic Beanstalk
   - Elastic Beanstalk `almotawaj-prod`: the five services
   - RDS `almotawaj-db`, Kafka EC2 `almotawaj-kafka`
   - Parameter Store, CloudWatch, Resend
3. Mark public versus private, and the security groups between them.
4. Show the request path (browser → gateway → services → RDS) and the event path (Kafka → consumers).
5. Export as PNG to `docs/images/` and link it here and in the README.
