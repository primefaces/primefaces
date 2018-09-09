/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.primefaces.model.chartjs.javascript;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JavaScriptFunction {

	private final String function;

	public JavaScriptFunction(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}

	public static class JavaScriptFunctionTypeAdapter extends TypeAdapter<JavaScriptFunction> {

		@Override
		public void write(JsonWriter jsonWriter, JavaScriptFunction javaScriptFunction) throws IOException {
			if (javaScriptFunction == null) {
				jsonWriter.nullValue();
				return;
			}

			jsonWriter.jsonValue(javaScriptFunction.getFunction());
		}

		@Override
		public JavaScriptFunction read(JsonReader jsonReader) {
			return null;
		}
	}
}
