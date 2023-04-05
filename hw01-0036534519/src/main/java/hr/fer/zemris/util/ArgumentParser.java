package hr.fer.zemris.util;

import java.util.*;

/**
 * Used for parsing program arguments
 * <p>
 * Example:
 * <ul>
 *     <li>program -flag1 -flag2 -arg param1 param2</li>
 * </ul>
 */
public class ArgumentParser {
    /**
     * Raw program arguments
     */
    private final List <String> args;
    /**
     * Mappings for argument -> parameters
     */
    private final HashMap<String, List<String>> map;
    /**
     * List of flags (arguments without parameters)
     */
    private final Set<String> flags;

    public ArgumentParser(List<String> arguments) {
        this.args = arguments;
        this.map = new HashMap<>();
        this.flags = new HashSet<>();
        map();
    }

    public ArgumentParser(String[] arguments) {
        this(List.of(arguments));
    }

    /**
     * @return set of argument names
     */
    public Set<String> getArgumentNames() {
        Set<String> argumentNames = new HashSet<>();
        argumentNames.addAll(flags);
        argumentNames.addAll(map.keySet());
        return argumentNames;
    }

    /**
     * @param flagName of the flag we ar checking
     * @return True if flag is given, false otherwise
     */
    public boolean getFlag(String flagName) {
        return flags.contains(flagName);
    }

    /**+
     * @param argumentName whose values we are looking for
     * @return argument value for particular argument name
     */
    public List<String> getArgumentValue(String argumentName) {
        return map.get(argumentName);
    }

    /**
     * Maps the flags and argument names with the values
     */
    public void map() {
        for(String arg: args) {
            if(arg.startsWith("-")) {
                if (args.indexOf(arg) == (args.size() - 1)) {
                    flags.add(arg.replace("-", ""));
                } else if (args.get(args.indexOf(arg)+1).startsWith("-")) {
                    flags.add(arg.replace("-", ""));
                } else {
                    List<String> argumentValues = new ArrayList<>();
                    int i = 1;
                    while(args.indexOf(arg)+i != args.size() && !args.get(args.indexOf(arg)+i).startsWith("-")) {
                        argumentValues.add(args.get(args.indexOf(arg)+i));
                        i++;
                    }
                    map.put(arg.replace("-", ""), argumentValues);
                }
            }
        }
    }
}