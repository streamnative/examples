## How to get OAuth2 options

When configuring and using OAuth2, we need to specify the following parameters:

- type
- issuerUrl
- audience
- privateKey
- clientId

For OAuth2 type, currently we only support `client_credentials`. So the value of the current type field is `client_credentials`

For privateKey, what we need to receive is the path of a private key data file. We can get this file in the following way:

```shell script
$ snctl auth export-service-account [NAME] [flags]

Flags:
  -h, --help              help for export-service-account
  -f, --key-file string   Path to the private key file.
      --no-wait           Skip waiting for service account readiness.
```

Output:

```text
Wrote private key file <Path of your private key file>
```

For `clientId` and `issuerUrl`, we can get the corresponding value from the privateKey file. For example:

```text
{
"type":"sn_service_account",
"client_id":"0123456789abcdefg",
"client_secret":"ABCDEFG-JzAFKtj0Dcub9KF1WKN-qhFHBvgfAU_123456789-KI9",
"client_email":"test@gtest.auth0.com",
"issuer_url":"https://test.auth0.com"
}
```

For `audience`, is the address of the accessed service.
