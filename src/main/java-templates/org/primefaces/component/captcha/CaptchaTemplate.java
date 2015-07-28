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
import org.primefaces.context.RequestContext;
import org.primefaces.component.captcha.Captcha;
import org.primefaces.context.PrimeExternalContext;
import org.primefaces.json.JSONObject;

    public final static String PUBLIC_KEY = "primefaces.PUBLIC_CAPTCHA_KEY";
    public final static String PRIVATE_KEY = "primefaces.PRIVATE_CAPTCHA_KEY";
    public final static String INVALID_MESSAGE_ID = "primefaces.captcha.INVALID";

    @Override
	protected void validateValue(FacesContext context, Object value) {
		super.validateValue(context, value);

        if(isValid()) {
            
            boolean result = false;
            
            try {
                URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String postBody = createPostParameters(context, value);

                OutputStream out = conn.getOutputStream();
                out.write(postBody.getBytes());
                out.flush();
                out.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = rd.readLine()) != null) {
                    response.append(inputLine);
                }
                
                JSONObject json = new JSONObject(response.toString());
                result = json.getBoolean("success");
                
                rd.close();
            }catch(Exception exception) {
                throw new FacesException(exception);
            }

            if(!result) {
                setValid(false);

                String validatorMessage = getValidatorMessage();
                FacesMessage msg = null;

                if(validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else {
                    Object[] params = new Object[2];
                    params[0] = MessageFactory.getLabel(context, this);
                    params[1] = (String)value;

                    msg = MessageFactory.getMessage(Captcha.INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                }

                context.addMessage(getClientId(context), msg);
            }
        }
        
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(requestContext.isAjaxRequest()) {
            requestContext.execute("grecaptcha.reset()");
        }
	}

    private String createPostParameters(FacesContext facesContext, Object value) throws UnsupportedEncodingException {
        String privateKey = facesContext.getExternalContext().getInitParameter(Captcha.PRIVATE_KEY);

        if(privateKey == null) {
            throw new FacesException("Cannot find private key for catpcha, use primefaces.PRIVATE_CAPTCHA_KEY context-param to define one");
        }

		StringBuilder postParams = new StringBuilder();
		postParams.append("secret=").append(URLEncoder.encode(privateKey, "UTF-8"));
		postParams.append("&response=").append(URLEncoder.encode((String) value, "UTF-8"));

        String params = postParams.toString();
        postParams.setLength(0);
        
		return params;
	}