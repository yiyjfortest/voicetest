package voice_test.launcher;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLauncher {
	
	public static final String SERVICE_PROVIDER_XML = "applicationContext.xml";
	
	public static void main(String[] args) throws Exception {
		
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(  
                new String[]{SERVICE_PROVIDER_XML});  
        context.start();
  
        System.out.println("Press any key to exit.");
        System.in.read();
	}
	
}
