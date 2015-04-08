package org.hl7.fhir.instance.model;

/*
  Copyright (c) 2011+, HL7, Inc.
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without modification, 
  are permitted provided that the following conditions are met:
  
   * Redistributions of source code must retain the above copyright notice, this 
     list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, 
     this list of conditions and the following disclaimer in the documentation 
     and/or other materials provided with the distribution.
   * Neither the name of HL7 nor the names of its contributors may be used to 
     endorse or promote products derived from this software without specific 
     prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
  
*/

// Generated on Thu, Apr 2, 2015 22:35+1100 for FHIR v0.5.0

import java.util.*;

import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.instance.model.annotations.ResourceDef;
import org.hl7.fhir.instance.model.annotations.SearchParamDefinition;
import org.hl7.fhir.instance.model.annotations.Block;
import org.hl7.fhir.instance.model.annotations.Child;
import org.hl7.fhir.instance.model.annotations.Description;
/**
 * An association between a patient and an organization / healthcare provider(s) during which time encounters may occur. The managing organization assumes a level of responsibility for the patient during this time.
 */
@ResourceDef(name="EpisodeOfCare", profile="http://hl7.org/fhir/Profile/EpisodeOfCare")
public class EpisodeOfCare extends DomainResource {

    public enum EpisodeOfCareStatus {
        /**
         * This episode of care is planned to start at the date specified in the period.start. During this status an organization may perform assessments to determine if they are eligible to receive services, or be organizing to make resources available to provide care services.
         */
        PLANNED, 
        /**
         * This episode has been placed on a waitlist, pending the episode being made active (or cancelled).
         */
        WAITLIST, 
        /**
         * This episode of care is current.
         */
        ACTIVE, 
        /**
         * This episode of care is on hold, the organization has limited responsibility for the patient (such as while on respite).
         */
        ONHOLD, 
        /**
         * This episode of care is finished at the organization is not expecting to be providing care to the patient. Can also be known as "closed", "completed" or other similar terms.
         */
        FINISHED, 
        /**
         * The episode of care was cancelled, or withdrawn from service, often selected during the planned stage as the patient may have gone elsewhere, or the circumstances have changed and the organization is unable to provide the care. It indicates that services terminated outside the planned/expected workflow.
         */
        CANCELLED, 
        /**
         * added to help the parsers
         */
        NULL;
        public static EpisodeOfCareStatus fromCode(String codeString) throws Exception {
            if (codeString == null || "".equals(codeString))
                return null;
        if ("planned".equals(codeString))
          return PLANNED;
        if ("waitlist".equals(codeString))
          return WAITLIST;
        if ("active".equals(codeString))
          return ACTIVE;
        if ("onhold".equals(codeString))
          return ONHOLD;
        if ("finished".equals(codeString))
          return FINISHED;
        if ("cancelled".equals(codeString))
          return CANCELLED;
        throw new Exception("Unknown EpisodeOfCareStatus code '"+codeString+"'");
        }
        public String toCode() {
          switch (this) {
            case PLANNED: return "planned";
            case WAITLIST: return "waitlist";
            case ACTIVE: return "active";
            case ONHOLD: return "onhold";
            case FINISHED: return "finished";
            case CANCELLED: return "cancelled";
            default: return "?";
          }
        }
        public String getSystem() {
          switch (this) {
            case PLANNED: return "";
            case WAITLIST: return "";
            case ACTIVE: return "";
            case ONHOLD: return "";
            case FINISHED: return "";
            case CANCELLED: return "";
            default: return "?";
          }
        }
        public String getDefinition() {
          switch (this) {
            case PLANNED: return "This episode of care is planned to start at the date specified in the period.start. During this status an organization may perform assessments to determine if they are eligible to receive services, or be organizing to make resources available to provide care services.";
            case WAITLIST: return "This episode has been placed on a waitlist, pending the episode being made active (or cancelled).";
            case ACTIVE: return "This episode of care is current.";
            case ONHOLD: return "This episode of care is on hold, the organization has limited responsibility for the patient (such as while on respite).";
            case FINISHED: return "This episode of care is finished at the organization is not expecting to be providing care to the patient. Can also be known as 'closed', 'completed' or other similar terms.";
            case CANCELLED: return "The episode of care was cancelled, or withdrawn from service, often selected during the planned stage as the patient may have gone elsewhere, or the circumstances have changed and the organization is unable to provide the care. It indicates that services terminated outside the planned/expected workflow.";
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
            case PLANNED: return "Planned";
            case WAITLIST: return "Waitlist";
            case ACTIVE: return "Active";
            case ONHOLD: return "On Hold";
            case FINISHED: return "Finished";
            case CANCELLED: return "Cancelled";
            default: return "?";
          }
        }
    }

  public static class EpisodeOfCareStatusEnumFactory implements EnumFactory<EpisodeOfCareStatus> {
    public EpisodeOfCareStatus fromCode(String codeString) throws IllegalArgumentException {
      if (codeString == null || "".equals(codeString))
            if (codeString == null || "".equals(codeString))
                return null;
        if ("planned".equals(codeString))
          return EpisodeOfCareStatus.PLANNED;
        if ("waitlist".equals(codeString))
          return EpisodeOfCareStatus.WAITLIST;
        if ("active".equals(codeString))
          return EpisodeOfCareStatus.ACTIVE;
        if ("onhold".equals(codeString))
          return EpisodeOfCareStatus.ONHOLD;
        if ("finished".equals(codeString))
          return EpisodeOfCareStatus.FINISHED;
        if ("cancelled".equals(codeString))
          return EpisodeOfCareStatus.CANCELLED;
        throw new IllegalArgumentException("Unknown EpisodeOfCareStatus code '"+codeString+"'");
        }
    public String toCode(EpisodeOfCareStatus code) {
      if (code == EpisodeOfCareStatus.PLANNED)
        return "planned";
      if (code == EpisodeOfCareStatus.WAITLIST)
        return "waitlist";
      if (code == EpisodeOfCareStatus.ACTIVE)
        return "active";
      if (code == EpisodeOfCareStatus.ONHOLD)
        return "onhold";
      if (code == EpisodeOfCareStatus.FINISHED)
        return "finished";
      if (code == EpisodeOfCareStatus.CANCELLED)
        return "cancelled";
      return "?";
      }
    }

    @Block()
    public static class EpisodeOfCareStatusHistoryComponent extends BackboneElement {
        /**
         * planned | waitlist | active | onhold | finished | cancelled.
         */
        @Child(name ="status", type={CodeType.class}, order=1, min=1, max=1)
        @Description(shortDefinition="planned | waitlist | active | onhold | finished | cancelled", formalDefinition="planned | waitlist | active | onhold | finished | cancelled." )
        protected Enumeration<EpisodeOfCareStatus> status;

        /**
         * The period during this EpisodeOfCare that the specific status applied.
         */
        @Child(name ="period", type={Period.class}, order=2, min=1, max=1)
        @Description(shortDefinition="The period during this EpisodeOfCare that the specific status applied", formalDefinition="The period during this EpisodeOfCare that the specific status applied." )
        protected Period period;

        private static final long serialVersionUID = -1192432864L;

      public EpisodeOfCareStatusHistoryComponent() {
        super();
      }

      public EpisodeOfCareStatusHistoryComponent(Enumeration<EpisodeOfCareStatus> status, Period period) {
        super();
        this.status = status;
        this.period = period;
      }

        /**
         * @return {@link #status} (planned | waitlist | active | onhold | finished | cancelled.). This is the underlying object with id, value and extensions. The accessor "getStatus" gives direct access to the value
         */
        public Enumeration<EpisodeOfCareStatus> getStatusElement() { 
          if (this.status == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create EpisodeOfCareStatusHistoryComponent.status");
            else if (Configuration.doAutoCreate())
              this.status = new Enumeration<EpisodeOfCareStatus>(new EpisodeOfCareStatusEnumFactory()); // bb
          return this.status;
        }

        public boolean hasStatusElement() { 
          return this.status != null && !this.status.isEmpty();
        }

        public boolean hasStatus() { 
          return this.status != null && !this.status.isEmpty();
        }

        /**
         * @param value {@link #status} (planned | waitlist | active | onhold | finished | cancelled.). This is the underlying object with id, value and extensions. The accessor "getStatus" gives direct access to the value
         */
        public EpisodeOfCareStatusHistoryComponent setStatusElement(Enumeration<EpisodeOfCareStatus> value) { 
          this.status = value;
          return this;
        }

        /**
         * @return planned | waitlist | active | onhold | finished | cancelled.
         */
        public EpisodeOfCareStatus getStatus() { 
          return this.status == null ? null : this.status.getValue();
        }

        /**
         * @param value planned | waitlist | active | onhold | finished | cancelled.
         */
        public EpisodeOfCareStatusHistoryComponent setStatus(EpisodeOfCareStatus value) { 
            if (this.status == null)
              this.status = new Enumeration<EpisodeOfCareStatus>(new EpisodeOfCareStatusEnumFactory());
            this.status.setValue(value);
          return this;
        }

        /**
         * @return {@link #period} (The period during this EpisodeOfCare that the specific status applied.)
         */
        public Period getPeriod() { 
          if (this.period == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create EpisodeOfCareStatusHistoryComponent.period");
            else if (Configuration.doAutoCreate())
              this.period = new Period(); // cc
          return this.period;
        }

        public boolean hasPeriod() { 
          return this.period != null && !this.period.isEmpty();
        }

        /**
         * @param value {@link #period} (The period during this EpisodeOfCare that the specific status applied.)
         */
        public EpisodeOfCareStatusHistoryComponent setPeriod(Period value) { 
          this.period = value;
          return this;
        }

        protected void listChildren(List<Property> childrenList) {
          super.listChildren(childrenList);
          childrenList.add(new Property("status", "code", "planned | waitlist | active | onhold | finished | cancelled.", 0, java.lang.Integer.MAX_VALUE, status));
          childrenList.add(new Property("period", "Period", "The period during this EpisodeOfCare that the specific status applied.", 0, java.lang.Integer.MAX_VALUE, period));
        }

      public EpisodeOfCareStatusHistoryComponent copy() {
        EpisodeOfCareStatusHistoryComponent dst = new EpisodeOfCareStatusHistoryComponent();
        copyValues(dst);
        dst.status = status == null ? null : status.copy();
        dst.period = period == null ? null : period.copy();
        return dst;
      }

      @Override
      public boolean equalsDeep(Base other) {
        if (!super.equalsDeep(other))
          return false;
        if (!(other instanceof EpisodeOfCareStatusHistoryComponent))
          return false;
        EpisodeOfCareStatusHistoryComponent o = (EpisodeOfCareStatusHistoryComponent) other;
        return compareDeep(status, o.status, true) && compareDeep(period, o.period, true);
      }

      @Override
      public boolean equalsShallow(Base other) {
        if (!super.equalsShallow(other))
          return false;
        if (!(other instanceof EpisodeOfCareStatusHistoryComponent))
          return false;
        EpisodeOfCareStatusHistoryComponent o = (EpisodeOfCareStatusHistoryComponent) other;
        return compareValues(status, o.status, true);
      }

      public boolean isEmpty() {
        return super.isEmpty() && (status == null || status.isEmpty()) && (period == null || period.isEmpty())
          ;
      }

  }

    @Block()
    public static class EpisodeOfCareCareTeamComponent extends BackboneElement {
        /**
         * The practitioner (or Organization) within the team.
         */
        @Child(name ="member", type={Practitioner.class, Organization.class}, order=1, min=0, max=1)
        @Description(shortDefinition="The practitioner (or Organization) within the team", formalDefinition="The practitioner (or Organization) within the team." )
        protected Reference member;

        /**
         * The actual object that is the target of the reference (The practitioner (or Organization) within the team.)
         */
        protected Resource memberTarget;

        /**
         * The role that this team member is taking within this episode of care.
         */
        @Child(name ="role", type={CodeableConcept.class}, order=2, min=0, max=Child.MAX_UNLIMITED)
        @Description(shortDefinition="The role that this team member is taking within this episode of care", formalDefinition="The role that this team member is taking within this episode of care." )
        protected List<CodeableConcept> role;

        /**
         * The period of time that this practitioner is performing some role within the episode of care.
         */
        @Child(name ="period", type={Period.class}, order=3, min=0, max=1)
        @Description(shortDefinition="The period of time that this practitioner is performing some role within the episode of care", formalDefinition="The period of time that this practitioner is performing some role within the episode of care." )
        protected Period period;

        private static final long serialVersionUID = -2134086895L;

      public EpisodeOfCareCareTeamComponent() {
        super();
      }

        /**
         * @return {@link #member} (The practitioner (or Organization) within the team.)
         */
        public Reference getMember() { 
          if (this.member == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create EpisodeOfCareCareTeamComponent.member");
            else if (Configuration.doAutoCreate())
              this.member = new Reference(); // cc
          return this.member;
        }

        public boolean hasMember() { 
          return this.member != null && !this.member.isEmpty();
        }

        /**
         * @param value {@link #member} (The practitioner (or Organization) within the team.)
         */
        public EpisodeOfCareCareTeamComponent setMember(Reference value) { 
          this.member = value;
          return this;
        }

        /**
         * @return {@link #member} The actual object that is the target of the reference. The reference library doesn't populate this, but you can use it to hold the resource if you resolve it. (The practitioner (or Organization) within the team.)
         */
        public Resource getMemberTarget() { 
          return this.memberTarget;
        }

        /**
         * @param value {@link #member} The actual object that is the target of the reference. The reference library doesn't use these, but you can use it to hold the resource if you resolve it. (The practitioner (or Organization) within the team.)
         */
        public EpisodeOfCareCareTeamComponent setMemberTarget(Resource value) { 
          this.memberTarget = value;
          return this;
        }

        /**
         * @return {@link #role} (The role that this team member is taking within this episode of care.)
         */
        public List<CodeableConcept> getRole() { 
          if (this.role == null)
            this.role = new ArrayList<CodeableConcept>();
          return this.role;
        }

        public boolean hasRole() { 
          if (this.role == null)
            return false;
          for (CodeableConcept item : this.role)
            if (!item.isEmpty())
              return true;
          return false;
        }

        /**
         * @return {@link #role} (The role that this team member is taking within this episode of care.)
         */
    // syntactic sugar
        public CodeableConcept addRole() { //3
          CodeableConcept t = new CodeableConcept();
          if (this.role == null)
            this.role = new ArrayList<CodeableConcept>();
          this.role.add(t);
          return t;
        }

    // syntactic sugar
        public EpisodeOfCareCareTeamComponent addRole(CodeableConcept t) { //3
          if (t == null)
            return this;
          if (this.role == null)
            this.role = new ArrayList<CodeableConcept>();
          this.role.add(t);
          return this;
        }

        /**
         * @return {@link #period} (The period of time that this practitioner is performing some role within the episode of care.)
         */
        public Period getPeriod() { 
          if (this.period == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create EpisodeOfCareCareTeamComponent.period");
            else if (Configuration.doAutoCreate())
              this.period = new Period(); // cc
          return this.period;
        }

        public boolean hasPeriod() { 
          return this.period != null && !this.period.isEmpty();
        }

        /**
         * @param value {@link #period} (The period of time that this practitioner is performing some role within the episode of care.)
         */
        public EpisodeOfCareCareTeamComponent setPeriod(Period value) { 
          this.period = value;
          return this;
        }

        protected void listChildren(List<Property> childrenList) {
          super.listChildren(childrenList);
          childrenList.add(new Property("member", "Reference(Practitioner|Organization)", "The practitioner (or Organization) within the team.", 0, java.lang.Integer.MAX_VALUE, member));
          childrenList.add(new Property("role", "CodeableConcept", "The role that this team member is taking within this episode of care.", 0, java.lang.Integer.MAX_VALUE, role));
          childrenList.add(new Property("period", "Period", "The period of time that this practitioner is performing some role within the episode of care.", 0, java.lang.Integer.MAX_VALUE, period));
        }

      public EpisodeOfCareCareTeamComponent copy() {
        EpisodeOfCareCareTeamComponent dst = new EpisodeOfCareCareTeamComponent();
        copyValues(dst);
        dst.member = member == null ? null : member.copy();
        if (role != null) {
          dst.role = new ArrayList<CodeableConcept>();
          for (CodeableConcept i : role)
            dst.role.add(i.copy());
        };
        dst.period = period == null ? null : period.copy();
        return dst;
      }

      @Override
      public boolean equalsDeep(Base other) {
        if (!super.equalsDeep(other))
          return false;
        if (!(other instanceof EpisodeOfCareCareTeamComponent))
          return false;
        EpisodeOfCareCareTeamComponent o = (EpisodeOfCareCareTeamComponent) other;
        return compareDeep(member, o.member, true) && compareDeep(role, o.role, true) && compareDeep(period, o.period, true)
          ;
      }

      @Override
      public boolean equalsShallow(Base other) {
        if (!super.equalsShallow(other))
          return false;
        if (!(other instanceof EpisodeOfCareCareTeamComponent))
          return false;
        EpisodeOfCareCareTeamComponent o = (EpisodeOfCareCareTeamComponent) other;
        return true;
      }

      public boolean isEmpty() {
        return super.isEmpty() && (member == null || member.isEmpty()) && (role == null || role.isEmpty())
           && (period == null || period.isEmpty());
      }

  }

    /**
     * Identifier(s) by which this EpisodeOfCare is known.
     */
    @Child(name ="identifier", type={Identifier.class}, order=0, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="Identifier(s) by which this EpisodeOfCare is known", formalDefinition="Identifier(s) by which this EpisodeOfCare is known." )
    protected List<Identifier> identifier;

    /**
     * planned | waitlist | active | onhold | finished | cancelled.
     */
    @Child(name ="status", type={CodeType.class}, order=1, min=1, max=1)
    @Description(shortDefinition="planned | waitlist | active | onhold | finished | cancelled", formalDefinition="planned | waitlist | active | onhold | finished | cancelled." )
    protected Enumeration<EpisodeOfCareStatus> status;

    /**
     * The status history for the EpisodeOfCare.
     */
    @Child(name ="statusHistory", type={}, order=2, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="The status history for the EpisodeOfCare", formalDefinition="The status history for the EpisodeOfCare." )
    protected List<EpisodeOfCareStatusHistoryComponent> statusHistory;

    /**
     * The type can be very important in processing as this could be used in determining if the EpisodeOfCare is relevant to specific government reporting, or other types of classifications.
     */
    @Child(name ="type", type={CodeableConcept.class}, order=3, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="Specific type of EpisodeOfCare", formalDefinition="The type can be very important in processing as this could be used in determining if the EpisodeOfCare is relevant to specific government reporting, or other types of classifications." )
    protected List<CodeableConcept> type;

    /**
     * The patient that this EpisodeOfCare applies to.
     */
    @Child(name ="patient", type={Patient.class}, order=4, min=1, max=1)
    @Description(shortDefinition="The patient that this EpisodeOfCare applies to", formalDefinition="The patient that this EpisodeOfCare applies to." )
    protected Reference patient;

    /**
     * The actual object that is the target of the reference (The patient that this EpisodeOfCare applies to.)
     */
    protected Patient patientTarget;

    /**
     * The organization that has assumed the specific responsibilities for the specified duration.
     */
    @Child(name ="managingOrganization", type={Organization.class}, order=5, min=0, max=1)
    @Description(shortDefinition="The organization that has assumed the specific responsibilities for the specified duration", formalDefinition="The organization that has assumed the specific responsibilities for the specified duration." )
    protected Reference managingOrganization;

    /**
     * The actual object that is the target of the reference (The organization that has assumed the specific responsibilities for the specified duration.)
     */
    protected Organization managingOrganizationTarget;

    /**
     * The interval during which the managing organization assumes the defined responsibility.
     */
    @Child(name ="period", type={Period.class}, order=6, min=0, max=1)
    @Description(shortDefinition="The interval during which the managing organization assumes the defined responsibility", formalDefinition="The interval during which the managing organization assumes the defined responsibility." )
    protected Period period;

    /**
     * A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.
     */
    @Child(name ="condition", type={Condition.class}, order=7, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for", formalDefinition="A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for." )
    protected List<Reference> condition;
    /**
     * The actual objects that are the target of the reference (A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.)
     */
    protected List<Condition> conditionTarget;


    /**
     * Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.
     */
    @Child(name ="referralRequest", type={ReferralRequest.class}, order=8, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="Referral Request(s) that this EpisodeOfCare manages activities within", formalDefinition="Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals." )
    protected List<Reference> referralRequest;
    /**
     * The actual objects that are the target of the reference (Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.)
     */
    protected List<ReferralRequest> referralRequestTarget;


    /**
     * The practitioner that is the care manager/care co-ordinator for this patient.
     */
    @Child(name ="careManager", type={Practitioner.class}, order=9, min=0, max=1)
    @Description(shortDefinition="The practitioner that is the care manager/care co-ordinator for this patient", formalDefinition="The practitioner that is the care manager/care co-ordinator for this patient." )
    protected Reference careManager;

    /**
     * The actual object that is the target of the reference (The practitioner that is the care manager/care co-ordinator for this patient.)
     */
    protected Practitioner careManagerTarget;

    /**
     * The list of practitioners that may be facilitating this episode of care for specific purposes.
     */
    @Child(name ="careTeam", type={}, order=10, min=0, max=Child.MAX_UNLIMITED)
    @Description(shortDefinition="The list of practitioners that may be facilitating this episode of care for specific purposes", formalDefinition="The list of practitioners that may be facilitating this episode of care for specific purposes." )
    protected List<EpisodeOfCareCareTeamComponent> careTeam;

    private static final long serialVersionUID = -1251791864L;

    public EpisodeOfCare() {
      super();
    }

    public EpisodeOfCare(Enumeration<EpisodeOfCareStatus> status, Reference patient) {
      super();
      this.status = status;
      this.patient = patient;
    }

    /**
     * @return {@link #identifier} (Identifier(s) by which this EpisodeOfCare is known.)
     */
    public List<Identifier> getIdentifier() { 
      if (this.identifier == null)
        this.identifier = new ArrayList<Identifier>();
      return this.identifier;
    }

    public boolean hasIdentifier() { 
      if (this.identifier == null)
        return false;
      for (Identifier item : this.identifier)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #identifier} (Identifier(s) by which this EpisodeOfCare is known.)
     */
    // syntactic sugar
    public Identifier addIdentifier() { //3
      Identifier t = new Identifier();
      if (this.identifier == null)
        this.identifier = new ArrayList<Identifier>();
      this.identifier.add(t);
      return t;
    }

    // syntactic sugar
    public EpisodeOfCare addIdentifier(Identifier t) { //3
      if (t == null)
        return this;
      if (this.identifier == null)
        this.identifier = new ArrayList<Identifier>();
      this.identifier.add(t);
      return this;
    }

    /**
     * @return {@link #status} (planned | waitlist | active | onhold | finished | cancelled.). This is the underlying object with id, value and extensions. The accessor "getStatus" gives direct access to the value
     */
    public Enumeration<EpisodeOfCareStatus> getStatusElement() { 
      if (this.status == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.status");
        else if (Configuration.doAutoCreate())
          this.status = new Enumeration<EpisodeOfCareStatus>(new EpisodeOfCareStatusEnumFactory()); // bb
      return this.status;
    }

    public boolean hasStatusElement() { 
      return this.status != null && !this.status.isEmpty();
    }

    public boolean hasStatus() { 
      return this.status != null && !this.status.isEmpty();
    }

    /**
     * @param value {@link #status} (planned | waitlist | active | onhold | finished | cancelled.). This is the underlying object with id, value and extensions. The accessor "getStatus" gives direct access to the value
     */
    public EpisodeOfCare setStatusElement(Enumeration<EpisodeOfCareStatus> value) { 
      this.status = value;
      return this;
    }

    /**
     * @return planned | waitlist | active | onhold | finished | cancelled.
     */
    public EpisodeOfCareStatus getStatus() { 
      return this.status == null ? null : this.status.getValue();
    }

    /**
     * @param value planned | waitlist | active | onhold | finished | cancelled.
     */
    public EpisodeOfCare setStatus(EpisodeOfCareStatus value) { 
        if (this.status == null)
          this.status = new Enumeration<EpisodeOfCareStatus>(new EpisodeOfCareStatusEnumFactory());
        this.status.setValue(value);
      return this;
    }

    /**
     * @return {@link #statusHistory} (The status history for the EpisodeOfCare.)
     */
    public List<EpisodeOfCareStatusHistoryComponent> getStatusHistory() { 
      if (this.statusHistory == null)
        this.statusHistory = new ArrayList<EpisodeOfCareStatusHistoryComponent>();
      return this.statusHistory;
    }

    public boolean hasStatusHistory() { 
      if (this.statusHistory == null)
        return false;
      for (EpisodeOfCareStatusHistoryComponent item : this.statusHistory)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #statusHistory} (The status history for the EpisodeOfCare.)
     */
    // syntactic sugar
    public EpisodeOfCareStatusHistoryComponent addStatusHistory() { //3
      EpisodeOfCareStatusHistoryComponent t = new EpisodeOfCareStatusHistoryComponent();
      if (this.statusHistory == null)
        this.statusHistory = new ArrayList<EpisodeOfCareStatusHistoryComponent>();
      this.statusHistory.add(t);
      return t;
    }

    // syntactic sugar
    public EpisodeOfCare addStatusHistory(EpisodeOfCareStatusHistoryComponent t) { //3
      if (t == null)
        return this;
      if (this.statusHistory == null)
        this.statusHistory = new ArrayList<EpisodeOfCareStatusHistoryComponent>();
      this.statusHistory.add(t);
      return this;
    }

    /**
     * @return {@link #type} (The type can be very important in processing as this could be used in determining if the EpisodeOfCare is relevant to specific government reporting, or other types of classifications.)
     */
    public List<CodeableConcept> getType() { 
      if (this.type == null)
        this.type = new ArrayList<CodeableConcept>();
      return this.type;
    }

    public boolean hasType() { 
      if (this.type == null)
        return false;
      for (CodeableConcept item : this.type)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #type} (The type can be very important in processing as this could be used in determining if the EpisodeOfCare is relevant to specific government reporting, or other types of classifications.)
     */
    // syntactic sugar
    public CodeableConcept addType() { //3
      CodeableConcept t = new CodeableConcept();
      if (this.type == null)
        this.type = new ArrayList<CodeableConcept>();
      this.type.add(t);
      return t;
    }

    // syntactic sugar
    public EpisodeOfCare addType(CodeableConcept t) { //3
      if (t == null)
        return this;
      if (this.type == null)
        this.type = new ArrayList<CodeableConcept>();
      this.type.add(t);
      return this;
    }

    /**
     * @return {@link #patient} (The patient that this EpisodeOfCare applies to.)
     */
    public Reference getPatient() { 
      if (this.patient == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.patient");
        else if (Configuration.doAutoCreate())
          this.patient = new Reference(); // cc
      return this.patient;
    }

    public boolean hasPatient() { 
      return this.patient != null && !this.patient.isEmpty();
    }

    /**
     * @param value {@link #patient} (The patient that this EpisodeOfCare applies to.)
     */
    public EpisodeOfCare setPatient(Reference value) { 
      this.patient = value;
      return this;
    }

    /**
     * @return {@link #patient} The actual object that is the target of the reference. The reference library doesn't populate this, but you can use it to hold the resource if you resolve it. (The patient that this EpisodeOfCare applies to.)
     */
    public Patient getPatientTarget() { 
      if (this.patientTarget == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.patient");
        else if (Configuration.doAutoCreate())
          this.patientTarget = new Patient(); // aa
      return this.patientTarget;
    }

    /**
     * @param value {@link #patient} The actual object that is the target of the reference. The reference library doesn't use these, but you can use it to hold the resource if you resolve it. (The patient that this EpisodeOfCare applies to.)
     */
    public EpisodeOfCare setPatientTarget(Patient value) { 
      this.patientTarget = value;
      return this;
    }

    /**
     * @return {@link #managingOrganization} (The organization that has assumed the specific responsibilities for the specified duration.)
     */
    public Reference getManagingOrganization() { 
      if (this.managingOrganization == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.managingOrganization");
        else if (Configuration.doAutoCreate())
          this.managingOrganization = new Reference(); // cc
      return this.managingOrganization;
    }

    public boolean hasManagingOrganization() { 
      return this.managingOrganization != null && !this.managingOrganization.isEmpty();
    }

    /**
     * @param value {@link #managingOrganization} (The organization that has assumed the specific responsibilities for the specified duration.)
     */
    public EpisodeOfCare setManagingOrganization(Reference value) { 
      this.managingOrganization = value;
      return this;
    }

    /**
     * @return {@link #managingOrganization} The actual object that is the target of the reference. The reference library doesn't populate this, but you can use it to hold the resource if you resolve it. (The organization that has assumed the specific responsibilities for the specified duration.)
     */
    public Organization getManagingOrganizationTarget() { 
      if (this.managingOrganizationTarget == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.managingOrganization");
        else if (Configuration.doAutoCreate())
          this.managingOrganizationTarget = new Organization(); // aa
      return this.managingOrganizationTarget;
    }

    /**
     * @param value {@link #managingOrganization} The actual object that is the target of the reference. The reference library doesn't use these, but you can use it to hold the resource if you resolve it. (The organization that has assumed the specific responsibilities for the specified duration.)
     */
    public EpisodeOfCare setManagingOrganizationTarget(Organization value) { 
      this.managingOrganizationTarget = value;
      return this;
    }

    /**
     * @return {@link #period} (The interval during which the managing organization assumes the defined responsibility.)
     */
    public Period getPeriod() { 
      if (this.period == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.period");
        else if (Configuration.doAutoCreate())
          this.period = new Period(); // cc
      return this.period;
    }

    public boolean hasPeriod() { 
      return this.period != null && !this.period.isEmpty();
    }

    /**
     * @param value {@link #period} (The interval during which the managing organization assumes the defined responsibility.)
     */
    public EpisodeOfCare setPeriod(Period value) { 
      this.period = value;
      return this;
    }

    /**
     * @return {@link #condition} (A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.)
     */
    public List<Reference> getCondition() { 
      if (this.condition == null)
        this.condition = new ArrayList<Reference>();
      return this.condition;
    }

    public boolean hasCondition() { 
      if (this.condition == null)
        return false;
      for (Reference item : this.condition)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #condition} (A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.)
     */
    // syntactic sugar
    public Reference addCondition() { //3
      Reference t = new Reference();
      if (this.condition == null)
        this.condition = new ArrayList<Reference>();
      this.condition.add(t);
      return t;
    }

    // syntactic sugar
    public EpisodeOfCare addCondition(Reference t) { //3
      if (t == null)
        return this;
      if (this.condition == null)
        this.condition = new ArrayList<Reference>();
      this.condition.add(t);
      return this;
    }

    /**
     * @return {@link #condition} (The actual objects that are the target of the reference. The reference library doesn't populate this, but you can use this to hold the resources if you resolvethemt. A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.)
     */
    public List<Condition> getConditionTarget() { 
      if (this.conditionTarget == null)
        this.conditionTarget = new ArrayList<Condition>();
      return this.conditionTarget;
    }

    // syntactic sugar
    /**
     * @return {@link #condition} (Add an actual object that is the target of the reference. The reference library doesn't use these, but you can use this to hold the resources if you resolvethemt. A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.)
     */
    public Condition addConditionTarget() { 
      Condition r = new Condition();
      if (this.conditionTarget == null)
        this.conditionTarget = new ArrayList<Condition>();
      this.conditionTarget.add(r);
      return r;
    }

    /**
     * @return {@link #referralRequest} (Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.)
     */
    public List<Reference> getReferralRequest() { 
      if (this.referralRequest == null)
        this.referralRequest = new ArrayList<Reference>();
      return this.referralRequest;
    }

    public boolean hasReferralRequest() { 
      if (this.referralRequest == null)
        return false;
      for (Reference item : this.referralRequest)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #referralRequest} (Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.)
     */
    // syntactic sugar
    public Reference addReferralRequest() { //3
      Reference t = new Reference();
      if (this.referralRequest == null)
        this.referralRequest = new ArrayList<Reference>();
      this.referralRequest.add(t);
      return t;
    }

    // syntactic sugar
    public EpisodeOfCare addReferralRequest(Reference t) { //3
      if (t == null)
        return this;
      if (this.referralRequest == null)
        this.referralRequest = new ArrayList<Reference>();
      this.referralRequest.add(t);
      return this;
    }

    /**
     * @return {@link #referralRequest} (The actual objects that are the target of the reference. The reference library doesn't populate this, but you can use this to hold the resources if you resolvethemt. Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.)
     */
    public List<ReferralRequest> getReferralRequestTarget() { 
      if (this.referralRequestTarget == null)
        this.referralRequestTarget = new ArrayList<ReferralRequest>();
      return this.referralRequestTarget;
    }

    // syntactic sugar
    /**
     * @return {@link #referralRequest} (Add an actual object that is the target of the reference. The reference library doesn't use these, but you can use this to hold the resources if you resolvethemt. Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.)
     */
    public ReferralRequest addReferralRequestTarget() { 
      ReferralRequest r = new ReferralRequest();
      if (this.referralRequestTarget == null)
        this.referralRequestTarget = new ArrayList<ReferralRequest>();
      this.referralRequestTarget.add(r);
      return r;
    }

    /**
     * @return {@link #careManager} (The practitioner that is the care manager/care co-ordinator for this patient.)
     */
    public Reference getCareManager() { 
      if (this.careManager == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.careManager");
        else if (Configuration.doAutoCreate())
          this.careManager = new Reference(); // cc
      return this.careManager;
    }

    public boolean hasCareManager() { 
      return this.careManager != null && !this.careManager.isEmpty();
    }

    /**
     * @param value {@link #careManager} (The practitioner that is the care manager/care co-ordinator for this patient.)
     */
    public EpisodeOfCare setCareManager(Reference value) { 
      this.careManager = value;
      return this;
    }

    /**
     * @return {@link #careManager} The actual object that is the target of the reference. The reference library doesn't populate this, but you can use it to hold the resource if you resolve it. (The practitioner that is the care manager/care co-ordinator for this patient.)
     */
    public Practitioner getCareManagerTarget() { 
      if (this.careManagerTarget == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create EpisodeOfCare.careManager");
        else if (Configuration.doAutoCreate())
          this.careManagerTarget = new Practitioner(); // aa
      return this.careManagerTarget;
    }

    /**
     * @param value {@link #careManager} The actual object that is the target of the reference. The reference library doesn't use these, but you can use it to hold the resource if you resolve it. (The practitioner that is the care manager/care co-ordinator for this patient.)
     */
    public EpisodeOfCare setCareManagerTarget(Practitioner value) { 
      this.careManagerTarget = value;
      return this;
    }

    /**
     * @return {@link #careTeam} (The list of practitioners that may be facilitating this episode of care for specific purposes.)
     */
    public List<EpisodeOfCareCareTeamComponent> getCareTeam() { 
      if (this.careTeam == null)
        this.careTeam = new ArrayList<EpisodeOfCareCareTeamComponent>();
      return this.careTeam;
    }

    public boolean hasCareTeam() { 
      if (this.careTeam == null)
        return false;
      for (EpisodeOfCareCareTeamComponent item : this.careTeam)
        if (!item.isEmpty())
          return true;
      return false;
    }

    /**
     * @return {@link #careTeam} (The list of practitioners that may be facilitating this episode of care for specific purposes.)
     */
    // syntactic sugar
    public EpisodeOfCareCareTeamComponent addCareTeam() { //3
      EpisodeOfCareCareTeamComponent t = new EpisodeOfCareCareTeamComponent();
      if (this.careTeam == null)
        this.careTeam = new ArrayList<EpisodeOfCareCareTeamComponent>();
      this.careTeam.add(t);
      return t;
    }

    // syntactic sugar
    public EpisodeOfCare addCareTeam(EpisodeOfCareCareTeamComponent t) { //3
      if (t == null)
        return this;
      if (this.careTeam == null)
        this.careTeam = new ArrayList<EpisodeOfCareCareTeamComponent>();
      this.careTeam.add(t);
      return this;
    }

      protected void listChildren(List<Property> childrenList) {
        super.listChildren(childrenList);
        childrenList.add(new Property("identifier", "Identifier", "Identifier(s) by which this EpisodeOfCare is known.", 0, java.lang.Integer.MAX_VALUE, identifier));
        childrenList.add(new Property("status", "code", "planned | waitlist | active | onhold | finished | cancelled.", 0, java.lang.Integer.MAX_VALUE, status));
        childrenList.add(new Property("statusHistory", "", "The status history for the EpisodeOfCare.", 0, java.lang.Integer.MAX_VALUE, statusHistory));
        childrenList.add(new Property("type", "CodeableConcept", "The type can be very important in processing as this could be used in determining if the EpisodeOfCare is relevant to specific government reporting, or other types of classifications.", 0, java.lang.Integer.MAX_VALUE, type));
        childrenList.add(new Property("patient", "Reference(Patient)", "The patient that this EpisodeOfCare applies to.", 0, java.lang.Integer.MAX_VALUE, patient));
        childrenList.add(new Property("managingOrganization", "Reference(Organization)", "The organization that has assumed the specific responsibilities for the specified duration.", 0, java.lang.Integer.MAX_VALUE, managingOrganization));
        childrenList.add(new Property("period", "Period", "The interval during which the managing organization assumes the defined responsibility.", 0, java.lang.Integer.MAX_VALUE, period));
        childrenList.add(new Property("condition", "Reference(Condition)", "A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for.", 0, java.lang.Integer.MAX_VALUE, condition));
        childrenList.add(new Property("referralRequest", "Reference(ReferralRequest)", "Referral Request(s) that are fulfilled by this EpisodeOfCare, incoming referrals.", 0, java.lang.Integer.MAX_VALUE, referralRequest));
        childrenList.add(new Property("careManager", "Reference(Practitioner)", "The practitioner that is the care manager/care co-ordinator for this patient.", 0, java.lang.Integer.MAX_VALUE, careManager));
        childrenList.add(new Property("careTeam", "", "The list of practitioners that may be facilitating this episode of care for specific purposes.", 0, java.lang.Integer.MAX_VALUE, careTeam));
      }

      public EpisodeOfCare copy() {
        EpisodeOfCare dst = new EpisodeOfCare();
        copyValues(dst);
        if (identifier != null) {
          dst.identifier = new ArrayList<Identifier>();
          for (Identifier i : identifier)
            dst.identifier.add(i.copy());
        };
        dst.status = status == null ? null : status.copy();
        if (statusHistory != null) {
          dst.statusHistory = new ArrayList<EpisodeOfCareStatusHistoryComponent>();
          for (EpisodeOfCareStatusHistoryComponent i : statusHistory)
            dst.statusHistory.add(i.copy());
        };
        if (type != null) {
          dst.type = new ArrayList<CodeableConcept>();
          for (CodeableConcept i : type)
            dst.type.add(i.copy());
        };
        dst.patient = patient == null ? null : patient.copy();
        dst.managingOrganization = managingOrganization == null ? null : managingOrganization.copy();
        dst.period = period == null ? null : period.copy();
        if (condition != null) {
          dst.condition = new ArrayList<Reference>();
          for (Reference i : condition)
            dst.condition.add(i.copy());
        };
        if (referralRequest != null) {
          dst.referralRequest = new ArrayList<Reference>();
          for (Reference i : referralRequest)
            dst.referralRequest.add(i.copy());
        };
        dst.careManager = careManager == null ? null : careManager.copy();
        if (careTeam != null) {
          dst.careTeam = new ArrayList<EpisodeOfCareCareTeamComponent>();
          for (EpisodeOfCareCareTeamComponent i : careTeam)
            dst.careTeam.add(i.copy());
        };
        return dst;
      }

      protected EpisodeOfCare typedCopy() {
        return copy();
      }

      @Override
      public boolean equalsDeep(Base other) {
        if (!super.equalsDeep(other))
          return false;
        if (!(other instanceof EpisodeOfCare))
          return false;
        EpisodeOfCare o = (EpisodeOfCare) other;
        return compareDeep(identifier, o.identifier, true) && compareDeep(status, o.status, true) && compareDeep(statusHistory, o.statusHistory, true)
           && compareDeep(type, o.type, true) && compareDeep(patient, o.patient, true) && compareDeep(managingOrganization, o.managingOrganization, true)
           && compareDeep(period, o.period, true) && compareDeep(condition, o.condition, true) && compareDeep(referralRequest, o.referralRequest, true)
           && compareDeep(careManager, o.careManager, true) && compareDeep(careTeam, o.careTeam, true);
      }

      @Override
      public boolean equalsShallow(Base other) {
        if (!super.equalsShallow(other))
          return false;
        if (!(other instanceof EpisodeOfCare))
          return false;
        EpisodeOfCare o = (EpisodeOfCare) other;
        return compareValues(status, o.status, true);
      }

      public boolean isEmpty() {
        return super.isEmpty() && (identifier == null || identifier.isEmpty()) && (status == null || status.isEmpty())
           && (statusHistory == null || statusHistory.isEmpty()) && (type == null || type.isEmpty())
           && (patient == null || patient.isEmpty()) && (managingOrganization == null || managingOrganization.isEmpty())
           && (period == null || period.isEmpty()) && (condition == null || condition.isEmpty()) && (referralRequest == null || referralRequest.isEmpty())
           && (careManager == null || careManager.isEmpty()) && (careTeam == null || careTeam.isEmpty())
          ;
      }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.EpisodeOfCare;
   }

  @SearchParamDefinition(name="organization", path="EpisodeOfCare.managingOrganization", description="The organization that has assumed the specific responsibilities of this EpisodeOfCare", type="reference" )
  public static final String SP_ORGANIZATION = "organization";
  @SearchParamDefinition(name="patient", path="EpisodeOfCare.patient", description="The patient that this EpisodeOfCare applies to", type="reference" )
  public static final String SP_PATIENT = "patient";
  @SearchParamDefinition(name="condition", path="EpisodeOfCare.condition", description="A list of conditions/problems/diagnoses that this episode of care is intended to be providing care for", type="reference" )
  public static final String SP_CONDITION = "condition";
  @SearchParamDefinition(name="status", path="EpisodeOfCare.status", description="The current status of the Episode of Care as provided (does not check the status history collection)", type="token" )
  public static final String SP_STATUS = "status";
  @SearchParamDefinition(name="care-manager", path="EpisodeOfCare.careManager", description="The practitioner that is the care manager/care co-ordinator for this patient", type="reference" )
  public static final String SP_CAREMANAGER = "care-manager";
  @SearchParamDefinition(name="type", path="EpisodeOfCare.type", description="Specific type of EpisodeOfCare", type="token" )
  public static final String SP_TYPE = "type";
  @SearchParamDefinition(name="date", path="EpisodeOfCare.period", description="The provided date search value falls within the episode of care's period", type="date" )
  public static final String SP_DATE = "date";
  @SearchParamDefinition(name="incomingreferral", path="EpisodeOfCare.referralRequest", description="Incoming Referral Request", type="reference" )
  public static final String SP_INCOMINGREFERRAL = "incomingreferral";
  @SearchParamDefinition(name="identifier", path="EpisodeOfCare.identifier", description="Identifier(s) by which this EpisodeOfCare is known", type="token" )
  public static final String SP_IDENTIFIER = "identifier";
  @SearchParamDefinition(name="team-member", path="EpisodeOfCare.careTeam.member", description="A Practitioner or Organization allocated to the care team for this EpisodeOfCare", type="reference" )
  public static final String SP_TEAMMEMBER = "team-member";

}

