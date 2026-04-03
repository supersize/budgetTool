output "instance_public_ip" {
  value       = aws_instance.budgetTool.public_ip
  description = "The public IP of the web server"
}
