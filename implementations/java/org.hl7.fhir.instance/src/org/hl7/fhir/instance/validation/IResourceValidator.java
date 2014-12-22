package org.hl7.fhir.instance.validation;

import java.util.List;

import org.hl7.fhir.instance.model.Profile;
import org.w3c.dom.Element;

public interface IResourceValidator {

  public enum CheckDisplayOption {
    Ignore,
    Check,
    CheckCaseAndSpace,
    CheckCase,
    CheckSpace
  }

  /**
   * how much to check displays for coded elements 
   * @return
   */
  CheckDisplayOption getCheckDisplay();

  /**
   * how much to check displays for coded elements 
   * @return
   */
  void setCheckDisplay(CheckDisplayOption checkDisplay);

  /**
   * whether the resource must have an id or not (depends on context)
   * 
   * @return
   */
  boolean isRequireResourceId();
  void setRequireResourceId(boolean requiresResourceId);
  
  
  /**
   * Given a DOM element, return a list of errors in the resource
   * 
   * @param errors
   * @param elem
   * @throws Exception - if the underlying infrastructure fails (not if the resource is invalid)
   */
  void validate(List<ValidationMessage> errors, Element element) throws Exception;

  /**
   * Given a DOM element, return a list of errors in the resource
   * 
   * @param errors
   * @param elem
   * @throws Exception - if the underlying infrastructure fails (not if the resource is invalid)
   */
  List<ValidationMessage> validate(Element element) throws Exception;

  /**
   * Given a DOM element, return a list of errors in the resource 
   * with regard to the specified profile (by logical identifier)
   *  
   * @param errors
   * @param element
   * @param profile
   * @throws Exception - if the underlying infrastructure fails, or the profile can't be found (not if the resource is invalid)
   */
  void validate(List<ValidationMessage> errors, Element element, String profile) throws Exception;

  /**
   * Given a DOM element, return a list of errors in the resource 
   * with regard to the specified profile (by logical identifier)
   *  
   * @param errors
   * @param element
   * @param profile
   * @throws Exception - if the underlying infrastructure fails, or the profile can't be found (not if the resource is invalid)
   */
	List<ValidationMessage> validate(Element element, String profile) throws Exception;

  /**
   * Given a DOM element, return a list of errors in the resource 
   * with regard to the specified profile 
   *  
   * @param errors
   * @param element
   * @param profile
   * @throws Exception - if the underlying infrastructure fails (not if the resource is invalid)
   */
  void validate(List<ValidationMessage> errors, Element element, Profile profile) throws Exception;

  /**
   * Given a DOM element, return a list of errors in the resource 
   * with regard to the specified profile
   *  
   * @param errors
   * @param element
   * @param profile
   * @throws Exception - if the underlying infrastructure fails (not if the resource is invalid)
   */
  List<ValidationMessage> validate(Element element, Profile profile) throws Exception;


}