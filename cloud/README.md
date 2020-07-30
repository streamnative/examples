## How to get OAuth2 options

When configuring and using OAuth2, we need to specify the following parameters:

- `type`
- `issuerUrl`
- `audience`
- `privateKey`
- `clientId`

For the OAuth2 `type` field, currently we only support `client_credentials`. So the value of the current type field is `client_credentials`

For the `privateKey` field, what we need to get the path of a private key data file. The following example shows how to get this file:

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

For the `clientId` and `issuerUrl` fields, we can get the corresponding value from the `privateKey` file. The following example shows how to get the `clientId` and `issueUrl` fields.

```text
{
"type":"sn_service_account",
"client_id":"0123456789abcdefg",
"client_secret":"ABCDEFG-JzAFKtj0Dcub9KF1WKN-qhFHBvgfAU_123456789-KI9",
"client_email":"test@gtest.auth0.com",
"issuer_url":"https://test.auth0.com"
}
```

For the `audience` field, is the address of the accessed service.
