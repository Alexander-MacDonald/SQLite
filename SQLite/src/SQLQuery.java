import java.util.Arrays;

public class SQLQuery {
    SQLQuery[] subQueries = {};
    String[] queryArgs = {};
    String query = "";
    int depth = 0;

    public SQLQuery() {
        System.out.println("SYSTEM:\tNo cmd provided, exiting...");
        System.exit(0);
    }

    public SQLQuery(String cmd, int depth) {
        cmd = cmd.replace(",", "");
        this.depth = depth;
        subQueries = findNestedQueries(cmd, this.depth + 1);
        query = isolateQuery(cmd).toUpperCase();
        classifyQuery();
    }

    //Creates a tree structure with the original command as the root node
    //cmd is the query that is at a certain depth level (0 being the intial command, 1 being nested, 2 being nested in the nested query...)
    private SQLQuery[] findNestedQueries(String cmd, int depth) {
        //Local depth is used to track when the sought depth is being tracked
        //EX:   0th layer searches for layer 1 in its depth and starts at local depth 0
        //      1st layer searches for layer 2 in its depth and starts at local depth 1...
        int localDepth = depth - 1;
        //query index tracks the # of subqueries in the command to differentiate between one nested query and another
        //if the local query has more than one query at the same depth, this tracks it
        //EX:   select * from {select a, b, c from A join B on A.b = B.b} where b > 10 and c in {select c from B where c = 5} order by a
        //      Depth: 1 | Index: 0 - select a, b, c from A join B on A.b = B.b
        //      Depth: 1 | Index: 1 - select c from B where c = 5
        int queryIndex = 0;
        //Tracks the different queries with logic mentioned above
        String[] subqueries = new String[64];
        //Fills array with empty string to avoid null appending
        Arrays.fill(subqueries, "");

        //for char in provided cmd
        for(int i = 0; i < cmd.length(); i++) {
            //if you are entering a subquery
            if(cmd.charAt(i) == '{') {
                //increase local depth
                localDepth++;
                //if you're at the depth you're seeking dont append '{'
                if(localDepth == depth) {
                    continue;
                }
            }
            //if you are exiting a subquery
            if(cmd.charAt(i) == '}') {
                //reduce local depth
                localDepth--;
                //if you're exiting a query AND you're entering surface depth again, you've completed a query
                //This assumes query correctness, correctness is checked in isolateQuery()
                if(localDepth < depth) {
                    queryIndex++;
                }
            }
            //if your local depth is what your searching for or ANOTHER subquery, append it to the current building subquery
            if(localDepth >= depth) {
                subqueries[queryIndex] += cmd.charAt(i);
            }
        }
        SQLQuery[] ret = new SQLQuery[queryIndex];
        //for the number of queries found
        for(int j = 0; j < queryIndex; j++) {
            //if the query isnt null (no queries found)
            if(subqueries[j] != null) {
                //create a new query
                //this will call this function to infinitely search for nested queries in each query string
                ret[j] = new SQLQuery(subqueries[j], depth);
            }
        }
        return ret;
    }

    private String isolateQuery(String cmd) {
        //New Stringbuilder
        StringBuilder ret = new StringBuilder();
        //# of iterations for reference index
        int iter = 0;
        //Local depth is used to track when the sought depth is being tracked
        //EX:   0th layer searches for layer 1 in its depth and starts at local depth 0
        //      1st layer searches for layer 2 in its depth and starts at local depth 1...
        int localDepth = this.depth - 1;

        //for each char in the command
        for(int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            //if its a new nested cmd
            if(c == '{') {
                localDepth++;
                //if its at the depth you're searching
                if(localDepth == this.depth) {
                    ret.append(c).append(iter++);
                }
            }
            //if you're exiting a subquery reduce the depth
            if (c == '}') {
                localDepth--;
            }
            //if you're inside the depth you're looking, append it
            if(localDepth < this.depth) {
                ret.append(c);
            }
        }
        //return string builder
        return ret.toString();
    }

    private void classifyQuery() {
        queryArgs = query.split(" ");
        switch(queryArgs[0]) {
            case "SELECT":
                System.out.println("SYSTEM:\tSELECT Statement");
                //validateSelect();
                break;
            case "UPDATE":
                System.out.println("SYSTEM:\tUPDATE Statement");
                //validateUpdate();
                break;
            case "DELETE":
                System.out.println("SYSTEM:\tDELETE Statement");
                //validateDelete();
                break;
            case "INSERT":
                System.out.println("SYSTEM:\tINSERT Statement");
                //validateInsert();
                break;
            case "CREATE":
                System.out.println("SYSTEM:\tCREATE Statement");
                if(validateCreateQuery()) {
                    executeCreateQuery();
                }
                break;
            case "DROP":
                System.out.println("SYSTEM:\tDROP Statement");
                //validateDrop();
                break;
            case "ALTER":
                System.out.println("SYSTEM:\tALTER Statement");
                //validateAlter();
                break;
            case "INSPECT":
                System.out.println("SYSTEM:\tINSPECT Statement");
                //validateInspect();
                break;
            default:
                sysError("Unknown Command \"" + queryArgs[0] + "\"");
                return;
        }
    }

    private boolean validateCreateQuery() {

        return false;
    }

    private void executeCreateQuery() {
        
    }

    private void sysError() {
        System.out.println("SYSTEM ERROR:\tUnknown Error Occured");
    }

    private void sysError(String s) {
        System.out.println("SYSTEM ERROR:\t" + s);
    }

    public void print(int index) {
        for(int i = 0; i < subQueries.length; i++) {
            subQueries[i].print(i);
        }
        System.out.println("Depth: " + depth + " | Index: " + index + " | Query: " + query);
    }
}
