package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("user")
@CrossOrigin("*")
public class UserController {
    @PostMapping("/register")
}
