resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster"
}

resource "aws_ecr_repository" "ms_proposals" {
  name                 = "${var.project_name}/ms-proposals"
  image_tag_mutability = "MUTABLE"
}

resource "aws_cloudwatch_log_group" "ms_proposals" {
  name = "/ecs/${var.project_name}/ms-proposals"
}

resource "aws_ecs_task_definition" "ms_proposals" {
  family                   = "${var.project_name}-ms-proposals"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "2048"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "ms-proposals-container"
      image     = "${aws_ecr_repository.ms_proposals.repository_url}:latest"
      cpu       = 384
      memory    = 1792
      essential = true
      portMappings = [{
        containerPort = 8080,
        protocol      = "tcp"
      }]
      healthCheck = {
        command     = ["CMD-SHELL", "wget --spider -q -T 5 http://localhost:8080/proposals/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ms_proposals.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "app"
        }
      }
      dependsOn = [{ containerName = "xray-daemon", condition = "START" }]
    },
    {
      name      = "xray-daemon"
      image     = "public.ecr.aws/xray/aws-xray-daemon:3.x"
      cpu       = 128
      memory    = 256
      essential = true
      user      = "1337"
      portMappings = [{
        containerPort = 2000,
        protocol      = "udp"
      }]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ms_proposals.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "xray"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "ms_proposals" {
  name            = "${var.project_name}-ms-proposals-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.ms_proposals.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  health_check_grace_period_seconds = 120

  network_configuration {
    subnets         = [for s in aws_subnet.private : s.id]
    security_groups = [aws_security_group.ecs_services.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.ms_proposals.arn
    container_name   = "ms-proposals-container"
    container_port   = 8080
  }

  depends_on = [aws_lb_listener_rule.ms_proposals]
}

resource "aws_ecr_repository" "ms_audit" {
  name                 = "${var.project_name}/ms-audit"
  image_tag_mutability = "MUTABLE"
}

resource "aws_cloudwatch_log_group" "ms_audit" {
  name = "/ecs/${var.project_name}/ms-audit"
}

resource "aws_s3_bucket" "audit_logs" {
  bucket = "${var.project_name}-audit-logs-${data.aws_caller_identity.current.account_id}"
}

resource "aws_ecs_task_definition" "ms_audit" {
  family                   = "${var.project_name}-ms-audit"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([
    {
      name      = "ms-audit-container"
      image     = "${aws_ecr_repository.ms_audit.repository_url}:latest"
      cpu       = 128
      memory    = 768
      essential = true
      portMappings = [{
        containerPort = 8080,
        protocol      = "tcp"
      }]
      healthCheck = {
        command     = ["CMD-SHELL", "wget --spider -q -T 5 http://localhost:8080/audit/actuator/health || exit 1"]
        interval    = 30
        timeout     = 5
        retries     = 3
        startPeriod = 60
      }
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ms_audit.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "app"
        }
      }
      dependsOn = [{ containerName = "xray-daemon", condition = "START" }]
    },
    {
      name      = "xray-daemon"
      image     = "public.ecr.aws/xray/aws-xray-daemon:3.x"
      cpu       = 128
      memory    = 256
      essential = true
      user      = "1337"
      portMappings = [{
        containerPort = 2000,
        protocol      = "udp"
      }]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ms_audit.name
          "awslogs-region"        = var.region
          "awslogs-stream-prefix" = "xray"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "ms_audit" {
  name            = "${var.project_name}-ms-audit-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.ms_audit.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  health_check_grace_period_seconds = 120

  network_configuration {
    subnets         = [for s in aws_subnet.private : s.id]
    security_groups = [aws_security_group.ecs_services.id]
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.ms_audit.arn
    container_name   = "ms-audit-container"
    container_port   = 8080
  }

  depends_on = [aws_lb_listener_rule.ms_audit]
}
