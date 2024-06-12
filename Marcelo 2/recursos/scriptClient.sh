#!/bin/bash

# Função para atualizar a lista de pacotes disponíveis
atualizar_lista_pacotes() {
    echo "Atualizando a lista de pacotes..."
    if ! sudo apt update; then
        echo "Falha ao atualizar a lista de pacotes."
        return 1
    fi
    return 0
}

# Função para atualizar os pacotes instalados
atualizar_pacotes_instalados() {
    echo "Atualizando os pacotes instalados..."
    if ! sudo apt upgrade -y; then
        echo "Falha ao atualizar os pacotes instalados."
        return 1
    fi
    return 0
}

# Função para trocar a senha de um usuário
trocar_senha_usuario() {
    local usuario=$1
    local senha=$2
    echo "Trocando a senha do usuário $usuario..."
    if id "$usuario" &>/dev/null; then
        echo "$usuario:$senha" | sudo chpasswd || { echo "Falha ao trocar a senha do usuário $usuario."; return 1; }
        echo "Senha do usuário $usuario trocada com sucesso."
    else
        echo "Usuário $usuario não encontrado."
    fi
    return 0
}

# Função para verificar se o Java está instalado e instalar se necessário
verificar_instalar_java() {
    echo "Verificando se o Java está instalado..."
    if ! java -version &>/dev/null; then
        echo "Java não instalado. Instalando o Java..."
        if ! sudo apt install openjdk-17-jre -y; then
            echo "Falha na instalação do Java. Verifique os logs para mais informações."
            return 1
        fi
        echo "Java instalado com sucesso!"
    else
        echo "Java já está instalado."
    fi
    return 0
}

# Função para importar bibliotecas do repositório Git
importar_bibliotecas() {
    echo "Importando bibliotecas..."
    cd ~
    if ! git clone https://github.com/LiSync-Signage/JAR-Individual.git; then
        echo "Falha ao importar bibliotecas. Verifique os logs para mais informações."
        return 1
    fi
    echo "Bibliotecas importadas com sucesso!"
    return 0
}

# Função para instalar o Docker
instalar_docker() {
    echo "Instalando o Docker..."
    if ! sudo apt install docker.io -y; then
        echo "Falha na instalação do Docker. Verifique os logs para mais informações."
        return 1
    fi

    echo "Iniciando o serviço Docker..."
    if ! sudo systemctl start docker; then
        echo "Falha ao iniciar o serviço Docker. Verifique os logs para mais informações."
        return 1
    fi

    echo "Habilitando o Docker para iniciar com o sistema operacional..."
    if ! sudo systemctl enable docker; then
        echo "Falha ao habilitar o Docker para iniciar com o sistema operacional. Verifique os logs para mais informações."
        return 1
    fi

    return 0
}

# Função para configurar o MySQL no Docker
configurar_mysql_docker() {
    echo "Baixando a imagem do MySQL 5.7..."
    if ! sudo docker pull mysql:5.7; then
        echo "Falha ao baixar a imagem do MySQL. Verifique os logs para mais informações."
        return 1
    fi

    echo "Criando e iniciando o container MySQL..."
    if ! sudo docker run -d -p 3306:3306 --name ContainerBD -e MYSQL_DATABASE=lisyncDB -e MYSQL_ROOT_PASSWORD=urubu100 mysql:5.7; then
        echo "Falha ao criar o container MySQL. Verifique os logs para mais informações."
        return 1
    fi

    echo "Verificando o status do container..."
    local container_status=$(sudo docker ps -a --filter "name=ContainerBD" --format "{{.Status}}")
    if [[ $container_status == Up* ]]; then
        echo "O container foi criado com sucesso e está em execução."
        echo "Aguardando 30 segundos para o MySQL iniciar completamente..."
        sleep 30

        echo "Criando o usuário 'marcelo' e concedendo privilégios..."
        if ! sudo docker exec -i ContainerBD mysql -u root -p'urubu100' -e "CREATE USER 'marcelo'@'%' IDENTIFIED BY 'urubu100'; ALTER USER 'marcelo' IDENTIFIED WITH mysql_native_password BY 'urubu100'; GRANT ALL PRIVILEGES ON *.* TO 'marcelo'@'%'; FLUSH PRIVILEGES;"; then
            echo "Falha ao criar o usuário 'marcelo'. Verifique os logs para mais informações."
            return 1
        fi
        echo "Usuário 'marcelo' criado com sucesso e privilégios concedidos."

        echo "Executando o script SQL 'lisyncBD'..."
        if ! sudo docker exec -i ContainerBD sh -c 'mysql -u root -p"urubu100" lisyncDB < /home/ubuntu/JAR-Individual/Marcelo/recursos/lisyncDBNew.sql'; then
            echo "Falha ao executar o script SQL. Verifique os logs para mais informações."
            return 1
        fi
        echo "Script SQL 'lisyncBD' executado com sucesso."
    else
        echo "A criação do container falhou. Verificando os logs..."
        sudo docker logs ContainerBD
        return 1
    fi

    return 0
}

# Executar as funções na ordem necessária e verificar o sucesso
main() {
    atualizar_lista_pacotes || exit 1
    atualizar_pacotes_instalados || exit 1
    trocar_senha_usuario root urubu100 || exit 1
    trocar_senha_usuario ubuntu urubu100 || exit 1
    verificar_instalar_java || exit 1
    importar_bibliotecas || exit 1
    atualizar_lista_pacotes || exit 1
    instalar_docker || exit 1
    configurar_mysql_docker || exit 1

    echo "Script concluído com sucesso."
}

main
