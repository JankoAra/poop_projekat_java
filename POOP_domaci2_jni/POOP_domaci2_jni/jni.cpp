#include "application_Table.h"
#include <string>
#include "TableJNI.h"
using namespace std;

/*
U properties C++ -> Precompiled Headers -> Not using precompiled headers
*/

/*
Native metoda iz jave. Prima tabelu u CSV formatu i gradi internu tabelu.
U tabeli pronalazi sve formule i razresava ih. Vraca CSV string koji predstavlja
vrednosti polja u tabeli sa razresenim formulama.
*/
JNIEXPORT jstring JNICALL Java_application_Table_resolveTableFormulas
(JNIEnv* env, jobject, jstring csvTable) {
	string cstring = env->GetStringUTFChars(csvTable, nullptr);
	TableJNI* table = new TableJNI(cstring);
	table->resolveFormulas();
	string res = table->convertTableToCSV();
	jstring ret = env->NewStringUTF(res.c_str());
	delete table;
	return ret;
}