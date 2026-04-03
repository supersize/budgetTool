# configure security groups
resource "aws_security_group" "docker_sg" {
  name        = "docker_sg"
  description = "Allow HTTP, HTTPS and SSH for Docker containers"
  vpc_id      = "vpc-01f1edf5278761aee"

  # SSH (To access the server)
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # For better security, use your specific IP here
  }

  # HTTP (For Nginx/Web Container)
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS (For SSL)
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # For Nginx welcome page test
  ingress {
    from_port   = 6477
    to_port     = 6477
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Outbound (Allows the server to download Docker images from the internet)
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
