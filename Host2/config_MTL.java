package Host1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class config_MTL {

	static enum D_LOCATION{
		tor,mtl,otw
	}
	
	
	static ArrayList<String> MANAGER_ACCOUNT = new ArrayList<String>(){
		{
			add("MTLM9087");
			add("MTLM1001");
			add("MTLM1002");
			add("MTLM9000");
		}
	};
	
	
	static ArrayList<String> CUSTOMER_ACCOUNT = new ArrayList<String>(){
		{
			add("MTLC1000");
			add("MTLC1001");
			add("MTLC1002");
		}
	};

	static String MANAGER_ID = null;
	static String CUSTOMER_ID = null;
	static int REGISTRY_PORT = 1200;
	static String SERVER_NAME = "MTL";
	static int LOCAL_LISTENING_PORT = 6002;
	static Logger LOGGER = null;
	static FileHandler FH = null;
	static String DIR = "Server_Side_Log";
	
	static int SERVER_PORT_TOR = 6001;
	static int SERVER_PORT_MTL = 6002;
	static int SERVER_PORT_OTW= 6003;

}
