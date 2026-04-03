resource "aws_instance" "budgetTool" {
  # 내용은 비워두세요.
  ami                    = "ami-02dfbd4ff395f2a1b"
  instance_type          = "t3.micro"
  key_name               = "cicd-test"
  subnet_id              = "subnet-054fda52483becf75"
  
  # Link to the Security Group above
  vpc_security_group_ids = [aws_security_group.docker_sg.id]

  root_block_device {
    delete_on_termination = true     
    encrypted             = false    
    kms_key_id            = null
    volume_size           = 8
    volume_type           = "gp3"    
  }

  tags = {
    Name = "cicd-test"
  }

  # you don't need "sudo" cuz user_data runs as root!!!
  user_data = <<-EOF
    #!/bin/bash
    # Create and enable 2GB swap
    dd if=/dev/zero of=/swapfile bs=128M count=16
    chmod 600 /swapfile
    mkswap /swapfile
    swapon /swapfile
    echo "/swapfile swap swap defaults 0 0" >> /etc/fstab
    
    # Optional: Install Docker while you're at it
    yum update -y
    yum install docker -y               # For Amazon Linux 2023
    systemctl start docker
    systemctl enable docker
    usermod -a -G docker ec2-user
  EOF

  # Safety lock to prevent accidental deletion
  lifecycle {
    prevent_destroy = true
  }
}
