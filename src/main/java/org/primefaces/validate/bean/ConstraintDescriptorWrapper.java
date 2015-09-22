package org.primefaces.validate.bean;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;

public class ConstraintDescriptorWrapper<T extends Annotation> implements ConstraintDescriptor<T>{
  protected final ConstraintDescriptor<T> constraint;

  @SuppressWarnings("unchecked")
  public ConstraintDescriptorWrapper(ConstraintDescriptor<?> constraint){
    this.constraint = (ConstraintDescriptor<T>) constraint;
  }

  public T getAnnotation(){
    return constraint.getAnnotation();
  }

  public Set<Class<?>> getGroups(){
    return constraint.getGroups();
  }

  public Set<Class<? extends Payload>> getPayload(){
    return constraint.getPayload();
  }

  public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses(){
    return constraint.getConstraintValidatorClasses();
  }

  public Map<String, Object> getAttributes(){
    return constraint.getAttributes();
  }

  public Set<ConstraintDescriptor<?>> getComposingConstraints(){
    return constraint.getComposingConstraints();
  }

  public boolean isReportAsSingleViolation(){
    return constraint.isReportAsSingleViolation();
  }

}
