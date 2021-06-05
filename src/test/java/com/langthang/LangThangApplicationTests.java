package com.langthang;

import com.langthang.services.IStorageServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class LangThangApplicationTests {

    @Autowired
    private IStorageServices storageServices;

    @Test
    void contextLoads() {
        List<String> objectsKey = Arrays.asList("71645695_p0.png", "1621696953695_avatar.jpeg");
        storageServices.deleteImages(objectsKey);
    }

}
