package Server_tor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class config_TOR {

	static enum D_LOCATION{
		tor,mtl,otw
	}
	
	
	static ArrayList<String> MANAGER_ACCOUNT = new ArrayList<String>(){
		{
			add("TORM6785");
			add("TORM1001");
			add("TORM1002");
			add("TORM3456");
		}
	};
	
	
	static ArrayList<String> CUSTOMER_ACCOUNT = new ArrayList<String>(){
		{
			add("TORC1234");
			add("TORC1001");
			add("TORC1002");
		}
	};

	static String MANAGER_ID = null;
	static String CUSTOMER_ID = null;
	static int REGISTRY_PORT = 1200;
	static String SERVER_NAME = "TOR";
	static int LOCAL_LISTENING_PORT = 6001;
	static Logger LOGGER = null;
	static FileHandler FH = null;
	
	static int SERVER_PORT_TOR = 6001;
	static int SERVER_PORT_MTL = 6002;
	static int SERVER_PORT_OTW= 6003;

	

}
