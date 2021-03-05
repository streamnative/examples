// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package options

import (
	"errors"

	"github.com/apache/pulsar-client-go/pulsar"

	flag "github.com/spf13/pflag"
)

// AuthenticationFlags defines common authentication flags for Pulsar.
type AuthenticationFlags struct {

	// custom authentication
	AuthPlugin string
	AuthParams string

	// TLS mutual authentication
	TLSCertFile string
	TLSKeyFile  string

	// token-based authentication
	Token     string
	TokenFile string

	// OAuth 2.0-based authentication
	Oauth2Issuer   string
	Oauth2ClientID string
	Oauth2Audience string
	Oauth2KeyFile  string
}

func (f *AuthenticationFlags) AddFlags(flags *flag.FlagSet) {
	f.addPluginFlags(flags)
	f.addTokenFlags(flags)
	f.addTlsFlags(flags)
	f.addOAuth2Flags(flags)
}

func (f *AuthenticationFlags) addPluginFlags(flags *flag.FlagSet) {
	flags.StringVar(
		&f.AuthPlugin,
		"auth-plugin",
		f.AuthPlugin,
		"The plugin to use for plugin authentication")
	flags.StringVar(
		&f.AuthParams,
		"auth-params",
		f.AuthParams,
		"The parameters for plugin authentication")
}

func (f *AuthenticationFlags) addTokenFlags(flags *flag.FlagSet) {
	flags.StringVar(
		&f.Token,
		"token",
		f.Token,
		"The token to use for token authentication")
	flags.StringVar(
		&f.TokenFile,
		"token-file",
		f.TokenFile,
		"The file with a token to use for token authentication")
}

func (f *AuthenticationFlags) addTlsFlags(flags *flag.FlagSet) {
	flags.StringVar(
		&f.TLSCertFile,
		"tls-cert-file",
		f.TLSCertFile,
		"The file with a TLS certificate for TLS authentication")
	flags.StringVar(
		&f.TLSKeyFile,
		"tls-key-file",
		f.TLSKeyFile,
		"The file with a TLS private key for TLS authentication")
}

func (f *AuthenticationFlags) addOAuth2Flags(flags *flag.FlagSet) {
	flags.StringVar(
		&f.Oauth2Issuer,
		"oauth2-issuer",
		"",
		"The issuer endpoint for OAuth2 authentication")
	flags.StringVar(
		&f.Oauth2Audience,
		"oauth2-audience",
		"", "The audience identifier for OAuth2 authentication")
	flags.StringVar(
		&f.Oauth2KeyFile,
		"oauth2-key-file",
		"", "The file with client credentials for OAuth2 authentication")
}

func (f *AuthenticationFlags) Validate() error {
	if f.TLSCertFile != "" || f.TLSKeyFile != "" {
		if f.TLSCertFile == "" {
			return errors.New("please specify --tls-cert-file for TLS-based authentication")
		}
		if f.TLSKeyFile == "" {
			return errors.New("please specify --tls-key-file for TLS-based authentication")
		}
	}

	if f.Oauth2Issuer != "" || f.Oauth2Audience != "" || f.Oauth2KeyFile != "" {
		if f.Oauth2Issuer == "" {
			return errors.New("please specify --oauth2-issuer for OAuth2-based authentication")
		}
		if f.Oauth2Audience == "" {
			return errors.New("please specify --oauth2-audience for OAuth2-based authentication")
		}
		if f.Oauth2KeyFile == "" {
			return errors.New("please specify --oauth2-key-file for OAuth2-based authentication")
		}
	}
	return nil
}

func (f *AuthenticationFlags) ToAuthenticationProvider() (pulsar.Authentication, error) {
	if f.AuthPlugin != "" {
		return pulsar.NewAuthentication(f.AuthPlugin, f.AuthParams)
	}

	if f.Token != "" {
		return pulsar.NewAuthenticationToken(f.Token), nil
	}
	if f.TokenFile != "" {
		return pulsar.NewAuthenticationTokenFromFile(f.TokenFile), nil
	}

	if f.TLSCertFile != "" || f.TLSKeyFile != "" {
		return pulsar.NewAuthenticationTLS(f.TLSCertFile, f.TLSKeyFile), nil
	}

	if f.Oauth2Issuer != "" || f.Oauth2Audience != "" || f.Oauth2KeyFile != "" {
		params := map[string]string{
			"type":       "client_credentials",
			"privateKey": f.Oauth2KeyFile,
			"issuerUrl":  f.Oauth2Issuer,
			"audience":   f.Oauth2Audience,
		}
		return pulsar.NewAuthenticationOAuth2(params), nil
	}

	return nil, nil
}
