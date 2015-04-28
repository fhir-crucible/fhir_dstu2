# see mongoid model for inclusion
module FHIR
  module Element
    # include elements from the core base
    # xml id
    def self.included(base)
      base.send(:field, :xmlId, type: String)
      base.send(:embeds_many, :extension, class_name:'FHIR::Extension', cyclic: true)
      base.send(:embeds_many, :modifierExtension, class_name:'FHIR::Extension', cyclic: true)
    end
  end
end
