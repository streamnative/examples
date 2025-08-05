class TokenCredentials:
    """
    The custom token credential
    """

    # the mechanism name is token
    TYPE = 'token'

    def __init__(self, token):
        """
        set your token
        """
        self._token = token
        self.erase_on_connect = False

    def response_for(self, start):
        if self.TYPE.encode('utf-8') not in start.mechanisms.split():
            return None, None

        # encode token
        response_bytes = self._token.encode('utf-8')

        # return mechanism name and encoded token
        return self.TYPE, response_bytes

    def erase_credentials(self):
        pass
