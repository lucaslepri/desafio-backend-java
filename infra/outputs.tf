output "alb_dns_name" {
  description = "DNS pblico do Appilcatinon Load Balancer"
  value       = aws_lb.main.dns_name
}

output "ms_proposals_url" {
  description = "URL para acessar o servico de propostas"
  value       = "http://${aws_lb.main.dns_name}/proposals"
}

output "ms_audit_url" {
  description = "URL para acessar o servico de auditoria"
  value       = "http://${aws_lb.main.dns_name}/audit"
}
