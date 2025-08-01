resource "aws_lb" "main" {
  name               = "${var.project_name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = [for s in aws_subnet.public : s.id]
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"
    fixed_response {
      content_type = "text/plain"
      message_body = "Caminho nao encontrado."
      status_code  = "404"
    }
  }
}

resource "aws_lb_target_group" "ms_proposals" {
  name        = "${var.project_name}-tg-proposals"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path = "/proposals/actuator/health"
    interval            = 60
    timeout             = 10
    healthy_threshold   = 2
    unhealthy_threshold = 4
  }
}

resource "aws_lb_target_group" "ms_audit" {
  name        = "${var.project_name}-tg-audit"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path = "/audit/actuator/health"
    interval            = 60
    timeout             = 10
    healthy_threshold   = 2
    unhealthy_threshold = 4
  }
}

resource "aws_lb_listener_rule" "ms_proposals" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ms_proposals.arn
  }

  condition {
    path_pattern {
      values = ["/proposals*"]
    }
  }
}

resource "aws_lb_listener_rule" "ms_audit" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 101

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ms_audit.arn
  }

  condition {
    path_pattern {
      values = ["/audit*"]
    }
  }
}
