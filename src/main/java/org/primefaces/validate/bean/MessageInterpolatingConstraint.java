package org.primefaces.validate.bean;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;

public class MessageInterpolatingConstraint extends ConstraintDescriptorWrapper<Annotation>{
  private static final String MESSAGE = "message";
  private MessageInterpolator interpolator;

  public MessageInterpolatingConstraint(MessageInterpolator interpolator, ConstraintDescriptor<?> constraint){
    super(constraint);
    this.interpolator = interpolator;
  }
  
  @Override
  public Map<String, Object> getAttributes(){
    Map<String, Object> attrs = super.getAttributes();
    Object message = attrs.remove(MESSAGE);
    if (message != null){
      if (message instanceof String){
        String template = (String) message;
        message = interpolator.interpolate(template, getContext());
      }
      attrs.put(MESSAGE, message);
    }
    return attrs;
  }
  
  private MessageInterpolator.Context getContext(){
    return new MessageInterpolator.Context(){
      public ConstraintDescriptor<?> getConstraintDescriptor(){
        return constraint;
      }

      public Object getValidatedValue(){
        return null;
      }
    };
  }

}
