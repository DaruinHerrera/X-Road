/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xroad.restapi.openapi;

import ee.ria.xroad.signer.protocol.dto.KeyInfo;
import ee.ria.xroad.signer.protocol.dto.TokenInfo;

import lombok.extern.slf4j.Slf4j;
import org.niis.xroad.restapi.converter.KeyConverter;
import org.niis.xroad.restapi.converter.TokenConverter;
import org.niis.xroad.restapi.exceptions.BadRequestException;
import org.niis.xroad.restapi.openapi.model.Key;
import org.niis.xroad.restapi.openapi.model.Token;
import org.niis.xroad.restapi.openapi.model.TokenPassword;
import org.niis.xroad.restapi.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * tokens controller
 */
@Controller
@RequestMapping("/api")
@Slf4j
@PreAuthorize("denyAll")
public class TokensApiController implements TokensApi {

    private final TokenService tokenService;
    private final TokenConverter tokenConverter;
    private final KeyConverter keyConverter;

    /**
     * TokensApiController constructor
     * @param tokenService
     * @param tokenConverter
     * @param keyConverter
     */

    @Autowired
    public TokensApiController(TokenService tokenService,
            TokenConverter tokenConverter,
            KeyConverter keyConverter) {
        this.tokenService = tokenService;
        this.tokenConverter = tokenConverter;
        this.keyConverter = keyConverter;
    }

    @PreAuthorize("hasAuthority('VIEW_KEYS')")
    @Override
    public ResponseEntity<List<Token>> getTokens() {
        List<TokenInfo> tokenInfos = null;
        try {
            tokenInfos = tokenService.getAllTokens();
        } catch (Exception e) {
            throw new RuntimeException("exception while reading tokens", e);
        }
        List<Token> tokens = tokenConverter.convert(tokenInfos);
        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_KEYS')")
    public ResponseEntity<Token> getToken(String id) {
        Token token = getTokenFromService(id);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('VIEW_KEYS')")
    public ResponseEntity<Key> getKey(String tokenId, String keyId) {
        Key key = getKeyFromService(tokenId, keyId);
        return new ResponseEntity<>(key, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ACTIVATE_TOKEN')")
    @Override
    public ResponseEntity<Token> loginToken(String id, TokenPassword tokenPassword) {
        if (tokenPassword == null
                || tokenPassword.getPassword() == null
                || tokenPassword.getPassword().isEmpty()) {
            throw new BadRequestException("Missing token password");
        }
        char[] password = tokenPassword.getPassword().toCharArray();
        tokenService.activateToken(id, password);
        Token token = getTokenFromService(id);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DEACTIVATE_TOKEN')")
    @Override
    public ResponseEntity<Token> logoutToken(String id) {
        tokenService.deactivateToken(id);
        Token token = getTokenFromService(id);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    private Token getTokenFromService(String id) {
        TokenInfo tokenInfo = null;
        try {
            tokenInfo = tokenService.getToken(id);
        } catch (TokenService.TokenNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException("unknown error when reading a token", t);
        }
        return tokenConverter.convert(tokenInfo);
    }

    private Key getKeyFromService(String tokenId, String keyId) {
        KeyInfo keyInfo = null;
        try {
            keyInfo = tokenService.getKey(tokenId, keyId);
        } catch (TokenService.TokenNotFoundException | TokenService.KeyNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException("unknown error when reading a token", t);
        }
        return keyConverter.convert(keyInfo);
    }

}
