from html import escape
from pathlib import Path

import diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.aws.compute import EC2, EC2ContainerRegistry, EC2ElasticIpAddress, ElasticBeanstalk
from diagrams.aws.database import RDS
from diagrams.aws.management import Cloudwatch, SystemsManagerParameterStore
from diagrams.aws.network import CloudFront
from diagrams.aws.security import WAF
from diagrams.aws.storage import SimpleStorageServiceS3Bucket
from diagrams.onprem.ci import Jenkins
from diagrams.onprem.client import User, Users
from diagrams.onprem.database import Postgresql
from diagrams.onprem.network import Internet
from diagrams.onprem.queue import Kafka
from diagrams.onprem.vcs import Github
from diagrams.programming.framework import React, Spring
from diagrams.saas.cdn import Cloudflare

OUTPUT_DIR = Path(__file__).resolve().parent.parent / "images" / "phase-04" / "architecture"
ICONS_DIR = Path(diagrams.__file__).resolve().parent.parent / "resources" / "aws"
AWS_ICON = ICONS_DIR / "aws.png"
VPC_ICON = ICONS_DIR / "network" / "vpc.png"
SECURITY_GROUP_ICON = ICONS_DIR / "general" / "ssl-padlock.png"

GRAPH = {"fontsize": "22", "fontname": "Helvetica", "pad": "0.6", "nodesep": "0.8", "ranksep": "1.2", "bgcolor": "white", "splines": "spline", "ordering": "out"}
NODE = {"fontsize": "13", "fontname": "Helvetica"}
EDGE = {"fontsize": "11", "fontname": "Helvetica", "color": "#5b6474", "tailport": "e", "headport": "w"}

AWS_CLOUD = {"bgcolor": "#f5f8ff", "pencolor": "#232f3e", "fontsize": "15", "style": "rounded"}
REGION = {"bgcolor": "#eef3fb", "pencolor": "#147eba", "fontsize": "14", "style": "dashed"}
VPC = {"bgcolor": "#f2f9ee", "pencolor": "#248814", "fontsize": "14", "style": "rounded"}
GROUP = {"bgcolor": "#ffffff", "pencolor": "#d0d7e2", "fontsize": "13", "style": "rounded"}
SECURITY_GROUP = {"bgcolor": "#fff8f0", "pencolor": "#dd344c", "fontsize": "13", "style": "dashed"}

HTTPS = Edge(label="HTTPS", color="#147eba", style="bold", tailport="e", headport="w")
EVENT = Edge(color="#e7157b", style="dashed", tailport="e", headport="w")
DEPLOY = Edge(color="#7aa116", style="bold", tailport="e", headport="w")


def group(icon, title):
    lines = "<BR ALIGN=\"LEFT\"/>".join(escape(line) for line in title.split("\n"))
    return (
        f'<<TABLE BORDER="0" CELLSPACING="4"><TR>'
        f'<TD FIXEDSIZE="TRUE" WIDTH="24" HEIGHT="24"><IMG SRC="{icon}" SCALE="TRUE"/></TD>'
        f'<TD ALIGN="LEFT">{lines}<BR ALIGN="LEFT"/></TD>'
        f"</TR></TABLE>>"
    )


def diagram(name, title):
    return Diagram(
        title,
        filename=str(OUTPUT_DIR / name),
        show=False,
        direction="LR",
        graph_attr=GRAPH,
        node_attr=NODE,
        edge_attr=EDGE,
    )


def overview():
    with diagram("01-overview", "Almotawaj Wallet on AWS: overview"):
        users = Users("Users")
        dns = Cloudflare("Cloudflare DNS")
        github = Github("GitHub")

        with Cluster(group(AWS_ICON, "AWS  |  eu-north-1 (Stockholm)"), graph_attr=AWS_CLOUD):
            cdn = CloudFront("CloudFront\nwallet.almotawaj.com")

            with Cluster("Application", graph_attr=GROUP):
                backend = ElasticBeanstalk("Elastic Beanstalk\n5 microservices")
                frontend = SimpleStorageServiceS3Bucket("S3\nReact frontend")

            with Cluster(group(SECURITY_GROUP_ICON, "Data  |  private"), graph_attr=VPC):
                kyc = SimpleStorageServiceS3Bucket("S3\nKYC files")
                kafka = Kafka("Kafka\non EC2")
                database = RDS("RDS\nPostgreSQL")

            with Cluster("Delivery and operations", graph_attr=GROUP):
                tools = Jenkins("Jenkins\non EC2")
                registry = EC2ContainerRegistry("ECR")
                secrets = SystemsManagerParameterStore("Parameter\nStore")
                logs = Cloudwatch("CloudWatch")

        users >> HTTPS >> dns >> cdn
        cdn >> Edge(tailport="e", headport="w") >> backend
        cdn >> Edge(tailport="e", headport="w") >> frontend
        backend >> Edge(tailport="e", headport="w") >> kyc
        backend >> Edge(tailport="e", headport="w", color="#e7157b", style="dashed") >> kafka
        backend >> Edge(tailport="e", headport="w") >> database
        github >> DEPLOY >> tools >> DEPLOY >> registry
        secrets - Edge(style="invis") - logs


def request_flow():
    with diagram("02-request-flow", "How a request reaches the app"):
        with Cluster("Internet", graph_attr=GROUP):
            user = User("Browser")
            dns = Cloudflare("Cloudflare DNS")

        with Cluster(group(AWS_ICON, "AWS"), graph_attr=AWS_CLOUD):
            cdn = CloudFront("CloudFront + ACM\nwallet.almotawaj.com")

            with Cluster("Elastic Beanstalk", graph_attr=GROUP):
                gateway = Spring("api-gateway\n/api/*, /auth/*")

            frontend = SimpleStorageServiceS3Bucket("S3 frontend\nall other paths")
            database = RDS("RDS")

        user >> HTTPS >> dns >> Edge(label="wallet → CloudFront") >> cdn
        cdn >> Edge(label="API, no cache") >> gateway >> database
        cdn >> Edge(label="static files") >> frontend


def inside_beanstalk():
    with diagram("03-inside-beanstalk", "Inside Elastic Beanstalk: the microservices"):
        cdn = CloudFront("CloudFront\n/api/*, /auth/*")

        with Cluster("EC2 instance  |  c7i-flex.large  |  5 Docker containers", graph_attr=GROUP):
            gateway = Spring("api-gateway\n:80")
            wallet = Spring("wallet-service :8084\nmoney, vouchers, payouts")
            identity = Spring("identity-service :8081\nauth, profile, KYC")
            audit = Spring("audit-service :8083\nreads audit-events")
            notification = Spring("notification-service :8082\nreads email-events")

        with Cluster("Data, messaging and email", graph_attr=GROUP):
            kafka = Kafka("Kafka")
            kyc = SimpleStorageServiceS3Bucket("S3 KYC files")
            database = RDS("RDS PostgreSQL\none database")
            email = Internet("Resend email")

        cdn >> gateway
        gateway >> wallet
        gateway >> identity
        gateway >> audit
        gateway >> Edge(style="invis") >> notification
        identity >> kyc
        identity >> database
        wallet >> database
        audit >> database
        identity >> EVENT >> kafka
        wallet >> EVENT >> kafka
        notification >> email


def kafka_events():
    with diagram("04-kafka-events", "Kafka events"):
        with Cluster("Producers", graph_attr=GROUP):
            identity = Spring("identity-service")
            wallet_producer = Spring("wallet-service")

        with Cluster("Kafka on EC2  |  almotawaj-kafka", graph_attr=GROUP):
            accounts = Kafka("account-events")
            emails = Kafka("email-events")
            audits = Kafka("audit-events")

        with Cluster("Consumers", graph_attr=GROUP):
            wallet_consumer = Spring("wallet-service\ncreates the wallet")
            notification = Spring("notification-service\nsends the email")
            audit = Spring("audit-service\nsaves the entry")

        with Cluster(group(SECURITY_GROUP_ICON, "Dead-letter topics"), graph_attr=SECURITY_GROUP):
            dead_letters = Kafka("<topic>-dlt\nafter 3 retries")

        identity >> EVENT >> accounts >> EVENT >> wallet_consumer
        identity >> EVENT >> emails >> EVENT >> notification
        identity >> EVENT >> audits
        wallet_producer >> EVENT >> audits >> EVENT >> audit
        [wallet_consumer, notification, audit] >> Edge(label="failed", style="dotted", color="#dd344c") >> dead_letters


def network_security():
    with diagram("05-network-security", "Network and security"):
        internet = Users("Internet")

        with Cluster(group(AWS_ICON, "AWS"), graph_attr=AWS_CLOUD):
            firewall = WAF("WAF\nrate limiting")
            tools_cdn = CloudFront("CloudFront\njenkins, pgadmin")
            cdn = CloudFront("CloudFront\nwallet")

            with Cluster(group(VPC_ICON, "Default VPC"), graph_attr=VPC):
                with Cluster(group(SECURITY_GROUP_ICON, "almotawaj-app-sg  |  80 open (Beanstalk)\nIAM role: almotawaj-eb-ec2-role"), graph_attr=SECURITY_GROUP):
                    backend = ElasticBeanstalk("Microservices")

                with Cluster(group(SECURITY_GROUP_ICON, "almotawaj-tools-sg  |  5050-8080 from CloudFront only\nIAM role: almotawaj-tools-role"), graph_attr=SECURITY_GROUP):
                    tools = EC2("Jenkins + pgAdmin")

                with Cluster(group(SECURITY_GROUP_ICON, "almotawaj-db-sg  |  5432 from app + tools"), graph_attr=SECURITY_GROUP):
                    database = RDS("RDS\nno public access")

                with Cluster(group(SECURITY_GROUP_ICON, "almotawaj-kafka-sg  |  9092 from app"), graph_attr=SECURITY_GROUP):
                    kafka = Kafka("Kafka")

            secrets = SystemsManagerParameterStore("Parameter Store\n7 SecureString")

        internet >> HTTPS >> firewall >> tools_cdn >> tools
        internet >> HTTPS >> cdn >> backend
        tools >> Edge(label="5432") >> database
        backend >> Edge(label="5432") >> database
        backend >> Edge(label="9092") >> kafka
        secrets >> Edge(label="secrets at start", style="dotted") >> backend


def cicd():
    with diagram("06-cicd", "CI/CD: from git push to production"):
        github = Github("GitHub\nmaster")

        with Cluster("Jenkins on EC2  |  Poll SCM every 2 min\nIAM role, no access keys", graph_attr=GROUP):
            jenkins = Jenkins("Jenkinsfile")

        with Cluster("services/** changed", graph_attr=GROUP):
            tests = Spring("Build and test\n5 services")
            registry = EC2ContainerRegistry("ECR\nbuild-N + latest")
            versions = SimpleStorageServiceS3Bucket("S3\nbuild-N.zip")
            backend = ElasticBeanstalk("Beanstalk\ndeploy build-N")

        with Cluster("frontend/** changed", graph_attr=GROUP):
            build = React("pnpm build")
            frontend = SimpleStorageServiceS3Bucket("S3\nsync")
            cdn = CloudFront("CloudFront\ninvalidate /*")

        github >> Edge(label="poll") >> jenkins
        jenkins >> DEPLOY >> tests >> DEPLOY >> registry >> DEPLOY >> versions >> DEPLOY >> backend
        jenkins >> DEPLOY >> build >> DEPLOY >> frontend >> DEPLOY >> cdn


def admin_tools():
    with diagram("07-admin-tools", "Admin tools: Jenkins and pgAdmin"):
        admin = User("Admin")
        dns = Cloudflare("Cloudflare DNS")

        with Cluster(group(AWS_ICON, "AWS"), graph_attr=AWS_CLOUD):
            jenkins_cdn = CloudFront("CloudFront\njenkins")
            pgadmin_cdn = CloudFront("CloudFront\npgadmin")

            with Cluster(group(VPC_ICON, "Default VPC"), graph_attr=VPC):
                address = EC2ElasticIpAddress("Elastic IP\n13.62.29.44")
                with Cluster("almotawaj-tools  |  t3.medium", graph_attr=GROUP):
                    pgadmin = Postgresql("pgAdmin :5050")
                    jenkins = Jenkins("Jenkins :8080")
                database = RDS("RDS PostgreSQL")

        admin >> HTTPS >> dns
        dns >> jenkins_cdn
        dns >> pgadmin_cdn
        jenkins_cdn >> address
        pgadmin_cdn >> address
        address >> Edge(label=":8080") >> jenkins
        address >> Edge(label=":5050") >> pgadmin
        pgadmin >> Edge(label="5432") >> database


if __name__ == "__main__":
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    overview()
    request_flow()
    inside_beanstalk()
    kafka_events()
    network_security()
    cicd()
    admin_tools()
