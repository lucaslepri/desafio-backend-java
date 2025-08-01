resource "aws_security_group" "alb" {
  name        = "${var.project_name}-alb-sg"
  description = "Controle de acesso para o Application Load Balancer"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Permitir HTTP da internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_services" {
  name        = "${var.project_name}-ecs-services-sg"
  description = "Controle de acesso para os servicos ECS"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Permitir trafego do ALB na porta da aplicacao"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "vpc_endpoints" {
  name        = "${var.project_name}-vpc-endpoints-sg"
  description = "Controle de acesso para os VPC Endpoints"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Permitir trafego dos servicos ECS para os endpoints"
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_services.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
