#  Copyright (c) 2011-2014, HL7, Inc & The MITRE Corporation
#  All rights reserved.
#  
#  Redistribution and use in source and binary forms, with or without modification, 
#  are permitted provided that the following conditions are met:
#  
#   * Redistributions of source code must retain the above copyright notice, this 
#     list of conditions and the following disclaimer.
#   * Redistributions in binary form must reproduce the above copyright notice, 
#     this list of conditions and the following disclaimer in the documentation 
#     and/or other materials provided with the distribution.
#   * Neither the name of HL7 nor the names of its contributors may be used to 
#     endorse or promote products derived from this software without specific 
#     prior written permission.
#  
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
#  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
#  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
#  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
#  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
#  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
#  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
#  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
#  POSSIBILITY OF SUCH DAMAGE.

class <ModelName>Controller
  before_filter :load, :only=>[:show, :update, :delete]
  @@xml_serializer = FHIR::Export::ModelSerializer.new
  
  def index

  end
  #need to change this to render atom feeds
  def show
    responds_to |format|
      format.xml {render xml: @<LowerCaseModelName>.to_xml}
      format.json {render json: @<LowerCaseModelName>}
    end

  end

  def update
    @updated_<LowerCaseModelName> = deserialize
    @<LowerCaseModelName>.update_attributes(@updated_<LowerCaseModelName>.attributes).save
  end

  def create
    @<LowerCaseModelName> = deserialize
    @<LowerCaseModelName>.save
    response.headers["Location"] = "#{BASE_URL}/<LowerCaseModelName>/#{@<LowerCaseModelName>.id}/_history/#{@<LowerCaseModelName>.version}"
  end

  def delete
    @<LowerCaseModelName>.destroy
    render status: 204
  end

  def validate
  end

  def vread
  end

  def history
  end


  def metadata
  end

  def tags
  end


  private 

  def deserialize
    if request.headers["Content-Type"] == "application/json"
      @<LowerCaseModelName>.new(params)
    elsif request.headers["Content-Type"] == "application/xml"
       @<LowerCaseModelName>.from_xml(Nokogiri::XML(request.body))
    end
  end

  def serialize
    
  end


  def load
    @<LowerCaseModelName> = <ModelName>.find(params[:id])
  end

end
