#ifndef TABLE_JNI
#define TABLE_JNI
#include <vector>
#include <string>
#include <map>
using namespace std;
class TableJNI{
public:
	TableJNI(string csvTable);
	TableJNI(string csvTable, int k);
	void printTableCSV();
	string getCellValue(int row, int col);
	string getCellValue(string name);
	static pair<int, int> cellNameToIndex(string name);
	static string indexToCellName(int row, int col);
	int getNumOfRows() {
		return data.size();
	}
	int getNumOfColumns() {
		return data[0].size();
	}
	string convertTableToCSV();
	map<pair<int, int>, string> extractFormulas();
	vector<string> extractTokens(string expression);
	map<pair<int, int>, vector<string>> extractExpressionTokens(map<pair<int, int>, string> formulaMap);
	bool hasValidOps(vector<string> ops);
	void resolveFormulas();
	
private:
	vector<vector<string>> data;
};

#endif