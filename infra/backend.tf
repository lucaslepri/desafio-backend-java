terraform {
  backend "s3" {
    bucket         = "desafio-itau-terraform-state"
    key            = "desafio/infra.tfstate"
    region         = "us-west-2"
    dynamodb_table = "desafio-itau-terraform-lock"
    encrypt        = true
  }
}