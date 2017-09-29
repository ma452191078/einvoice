package com.sdl.einvoice;

import com.sdl.einvoice.config.InvoiceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EinvoiceApplicationTests {

	@Autowired
	private InvoiceConfig invoiceConfig;

	@Test
	public void contextLoads() {
		System.out.println(invoiceConfig.getTaxpayerCode());
	}

}
