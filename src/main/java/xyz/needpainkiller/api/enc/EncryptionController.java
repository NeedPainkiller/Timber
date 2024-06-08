package xyz.needpainkiller.api.enc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.common.controller.CommonController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "10000000. 암호화", description = "ENCRYPTION")
@RequestMapping(value = "/api/v1/encryption", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
@RestController
@RequiredArgsConstructor
public class EncryptionController extends CommonController {

    @Autowired
    private StringEncryptor stringEncryptor;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping(value = "/enc")
    @Operation(description = "암호화")
    public ResponseEntity<Map<String, Object>> encryption(@RequestParam("payload") String payload, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        String enc = stringEncryptor.encrypt(payload);
        model.put("enc", enc);
        return ok(model);
    }

    @GetMapping(value = "/dec")
    @Operation(description = "복호화")

    public ResponseEntity<Map<String, Object>> decryption(@RequestParam("payload") String payload, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        String dec = stringEncryptor.decrypt(payload);
        model.put("dec", dec);
        return ok(model);
    }

    @GetMapping(value = "/passwd")
    @Operation(description = "암호 키 생성")
    public ResponseEntity<Map<String, Object>> passwd(@RequestParam("payload") String payload, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        String passwd = bCryptPasswordEncoder.encode(payload);
        model.put("passwd", passwd);
        return ok(model);
    }
}