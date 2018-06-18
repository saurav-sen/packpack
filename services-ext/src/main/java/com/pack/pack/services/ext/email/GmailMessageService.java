package com.pack.pack.services.ext.email;


/**
 * 
 * @author Saurav
 *
 */
/*@Component
@Lazy
@Scope("singleton")*/
public class GmailMessageService {

	/*private static Logger LOG = LoggerFactory
			.getLogger(GmailMessageService.class);

	private static final String APPLICATION_NAME = "PackPackApp";

	private static final String APP_SECRET_FILE_NAME = "client_secret_806379591288-l75ra2099up31uv3qgg8t0csvr292lib.apps.googleusercontent.com.json";

	private File dataStoreDir;

	private FileDataStoreFactory dsFactory;

	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private HttpTransport httpTransport;

	private Gmail service;

	private static final List<String> SCOPES = Arrays.asList(
			GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_COMPOSE,
			GmailScopes.GMAIL_MODIFY);*/

	//@PostConstruct
	/*public void initialize() throws Exception {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		String dataStoreDirPath = SystemPropertyUtil.getAppHome();
		if (dataStoreDirPath != null) {
			if (!dataStoreDirPath.endsWith(File.separator)) {
				dataStoreDirPath = dataStoreDirPath + File.separator;
			}
			dataStoreDirPath = dataStoreDirPath + "credentials"
					+ File.separator + "packpack";
		} else {
			dataStoreDirPath = ".." + File.separator + "credentials"
					+ File.separator + "packpack";
		}
		dataStoreDir = new File(dataStoreDirPath);
		dsFactory = new FileDataStoreFactory(dataStoreDir);

		Credential credential = authorize();
		service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	private Credential authorize() throws Exception {
		LOG.info("Authorizing with GMail");
		String appKeyFile = null;
		if (SystemPropertyUtil.getAppHome() == null) {
			appKeyFile = "D:/Saurav/packpack/services-ext/conf/"
					+ APP_SECRET_FILE_NAME;
		} else {
			appKeyFile = SystemPropertyUtil.getAppHome() + File.separator
					+ "conf" + File.separator + APP_SECRET_FILE_NAME;
		}
		InputStream in = new FileInputStream(new File(appKeyFile));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(dsFactory).setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize("user");
		LOG.debug("Credentials saved to " + dataStoreDir.getAbsolutePath());
		return credential;
	}*/
}