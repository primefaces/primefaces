import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.primefaces.util.MessageFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.faces.application.FacesMessage;
import javax.faces.FacesException;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

    public final static String PUBLIC_KEY = "primefaces.PUBLIC_CAPTCHA_KEY";
    public final static String PRIVATE_KEY = "primefaces.PRIVATE_CAPTCHA_KEY";
    public final static String INVALID_MESSAGE_ID = "primefaces.captcha.INVALID";

    public final static String OLD_PRIVATE_KEY = "org.primefaces.component.captcha.PRIVATE_KEY";

    private static final Logger logger = Logger.getLogger(Captcha.class.getName());

    @Override
	protected void validateValue(FacesContext context, Object value) {
		super.validateValue(context, value);

        if(isValid()) {

            String result = null;
            Verification verification = (Verification) value;

            try {
                URL url = new URL("http://api-verify.recaptcha.net/verify");
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String postBody = createPostParameters(context, verification);

                OutputStream out = conn.getOutputStream();
                out.write(postBody.getBytes());
                out.flush();
                out.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result = rd.readLine();
                rd.close();
            }catch(Exception exception) {
                throw new FacesException(exception);
            }

            boolean isValid = Boolean.valueOf(result);

            if(!isValid) {
                setValid(false);

                String validatorMessage = getValidatorMessage();
                FacesMessage msg = null;

                if(validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else {
                    Object[] params = new Object[2];
                    params[0] = MessageFactory.getLabel(context, this);
                    params[1] = verification.getAnswer();

                    msg = MessageFactory.getMessage(Captcha.INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                }

                context.addMessage(getClientId(context), msg);
            }
        }
	}

    private String createPostParameters(FacesContext facesContext, Verification verification) throws UnsupportedEncodingException {
		String challenge = verification.getChallenge();
		String answer = verification.getAnswer();
		String remoteAddress = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getRemoteAddr();
        String privateKey = null;
		String oldPrivateKey = facesContext.getExternalContext().getInitParameter(Captcha.OLD_PRIVATE_KEY);
        String newPrivateKey = facesContext.getExternalContext().getInitParameter(Captcha.PRIVATE_KEY);

        //Backward compatibility
        if(oldPrivateKey != null) {
            logger.warning("PrivateKey definition on captcha is deprecated, use primefaces.PRIVATE_CAPTCHA_KEY context-param instead");

            privateKey = oldPrivateKey;
        }
        else {
            privateKey = newPrivateKey;
        }

        if(privateKey == null) {
            throw new FacesException("Cannot find private key for catpcha, use primefaces.PRIVATE_CAPTCHA_KEY context-param to define one");
        }

		StringBuilder postParams = new StringBuilder();
		postParams.append("privatekey=").append(URLEncoder.encode(privateKey, "UTF-8"));
		postParams.append("&remoteip=").append(URLEncoder.encode(remoteAddress, "UTF-8"));
		postParams.append("&challenge=").append(URLEncoder.encode(challenge, "UTF-8"));
		postParams.append("&response=").append(URLEncoder.encode(answer, "UTF-8"));

		return postParams.toString();
	}