package com.yuypc.easyblog.controller;

import com.yuypc.easyblog.dto.req.CaptchaVerifyReqDTO;
import com.yuypc.easyblog.dto.resp.CaptchaRespDTO;
import com.yuypc.easyblog.utils.GenerateRandom;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
@Slf4j
public class CaptchaController {

    @GetMapping("/generate")
    public CaptchaRespDTO generateCaptcha(HttpSession session) throws IOException {
//        log.info("Session ID (generate): " + session.getId());
        int width = 160;
        int height = 60;
        String captchaText = GenerateRandom.generateRandomText();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 40));
        graphics.drawString(captchaText, 20, 45);

        // Adding some noise
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            graphics.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            graphics.drawOval(x, y, 2, 2);
        }

        session.setAttribute("captcha", captchaText);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        return new CaptchaRespDTO(base64Image);
    }

    @PostMapping("/verify")
    public boolean verifyCaptcha(@RequestBody CaptchaVerifyReqDTO request, HttpSession session) {
//        log.info("Session ID (verify): " + session.getId());
        String storedCaptcha = (String) session.getAttribute("captcha");
        return request.getCaptcha().equalsIgnoreCase(storedCaptcha);
    }
}
