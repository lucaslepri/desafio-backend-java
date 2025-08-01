variable "region" {
  description = "Regiao da AWS para provisionar os recursos."
  type        = string
  default     = "us-west-2"
}

variable "project_name" {
  description = "Nome base para todos os recursos."
  type        = string
  default     = "desafio-itau"
}

variable "vpc_cidr" {
  description = "Bloco CIDR para a nova VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Lista de Zonas de Disponibilidade para usar."
  type        = list(string)
  default     = ["us-west-2a", "us-west-2b"]
}
