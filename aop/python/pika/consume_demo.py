#!/usr/bin/env python3

import ssl

import pika
from pika.credentials import PlainCredentials

from token_credentials import TokenCredentials


def test_amqp_connection():
    """Simple AMQP connection test with amqp-test vhost"""
    host = "host"
    port = 5671
    vhost = "aoptest" # the namespace `public/aoptest` must be existing and the namespace bundle count must be 1

    print(f"Testing AMQP connection to {host}:{port} with vhost: {vhost}")

    try:
        token = 'TOKEN'
        credentials = TokenCredentials(token)
        pika.credentials.VALID_TYPES = [PlainCredentials, TokenCredentials]

        connection_params = pika.ConnectionParameters(
            host=host,
            port=port,
            virtual_host=vhost,
            credentials=credentials,  # 在这里添加 credentials 参数
            ssl_options=pika.SSLOptions(ssl.create_default_context()),
            socket_timeout=10
        )

        connection = pika.BlockingConnection(connection_params)
        channel = connection.channel()
        print("✓ Connection successful")

        count = 0
        # Get ten messages and break out
        for method_frame, properties, body in channel.consume('hello'):

            print(' [x] receive ', body)
            channel.basic_ack(method_frame.delivery_tag)

            # Escape out of the loop after 10 messages
            count += 1
            if count == 10:
                break

        channel.close()
        connection.close()
        return True

    except Exception as e:
        print(f"✗ Connection failed: {e}")
        return False


if __name__ == "__main__":
    test_amqp_connection()