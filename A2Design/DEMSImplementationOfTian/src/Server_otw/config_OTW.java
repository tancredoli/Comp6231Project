package Server_otw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class config_OTW {
	
	static enum D_LOCATION{
		tor,mtl,otw
	}
	
	
	static ArrayList<String> MANAGER_ACCOUNT = new ArrayList<String>(){
		{
			add("OTWM4560");
			add("OTWM1001");
			add("OTWM1002");
			add("OTWM9000");
			add("OTWM6785");
		}
	};
	
	
	static ArrayList<String> CUSTOMER_ACCOUNT = new ArrayList<String>(){
		{
			add("OTWC7890");
			add("OTWC1001");
			add("OTWC1002");
			add("OTWC1234");
		}
	};

	static String MANAGER_ID = null;
	static String CUSTOMER_ID = null;
	static int REGISTRY_PORT = 1200;
	static String SERVER_NAME = "OTW";
	static int LOCAL_LISTENING_PORT = 6003;
	static Logger LOGGER = null;
	static FileHandler FH = null;
	
	static int SERVER_PORT_TOR = 6001;
	static int SERVER_PORT_MTL = 6002;
	static int SERVER_PORT_OTW= 6003;


	

}
