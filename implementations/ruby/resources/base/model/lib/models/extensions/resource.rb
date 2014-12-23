# see mongoid model for inclusion
module FHIR
  module Resource
    # include elements from the base resource
    # narrative
    # contained array
    def self.included(base)
      base.send(:embeds_one, :text, class_name:'FHIR::Narrative')
      base.send(:field, :contained, type: Array)
    end
  end
end
