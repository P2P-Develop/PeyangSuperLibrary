package develop.p2p.lib.bukkit;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import develop.p2p.lib.bukkit.exception.SelectorException;
import develop.p2p.lib.bukkit.exception.SelectorInvalidException;
import develop.p2p.lib.bukkit.exception.SelectorMalformedException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 1.12.2以下のEntitySelector
 * MC 1.12.2 Serverからリバースエンジニアリング
 */
public class EntitySelector
{
    private static final Pattern tokenPattern;
    private static final Pattern intListPattern;
    private static final Pattern keyValueListPattern;
    private static final Pattern scorePattern;
    private static final String[] worldBindingArgs;
    private static final String[] usableArgs;

    static
    {
        tokenPattern = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)])?$");
        intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
        keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
        scorePattern = Pattern.compile("^score_(.+){1,16}(_min)?$");
        worldBindingArgs = new String[]{"x", "y", "z", "dx", "dy", "dz", "rm", "r"};
        usableArgs = new String[]{"x", "y", "z", "r", "rm", "dx", "dy", "dz", "tag", "team", "c", "l", "lm", "m", "name", "rx", "rxm", "ry", "rym", "type"};
    }

    public static Player matchOnePlayer(CommandSender sender,  String token) throws SelectorInvalidException, SelectorMalformedException
    {
        return (Player) matchOneEntity(sender, token, Player.class);
    }

    public static Entity matchOneEntity(CommandSender sender, String token, Class<? extends Entity> clazz) throws SelectorInvalidException, SelectorMalformedException
    {
        List<Entity> selectedEntities = matchEntities(sender, token, clazz);
        return selectedEntities.size() == 1 ? selectedEntities.get(0): null;
    }

    /**
     * セレクタがあってるか検証
     * @param selector セレクタ
     * @throws  SelectorInvalidException 変なセレクタが紛れ込んでる問題
     * @throws  SelectorMalformedException セレクタがセレクタとして成立してない問題
     */
    public static void validateSelector(String selector) throws SelectorInvalidException, SelectorMalformedException
    {
        Matcher tokenMatcher = tokenPattern.matcher(selector);
        if (!tokenMatcher.matches())
            throw new SelectorMalformedException();

        Map<String, String> keyMap = getArgumentMap(tokenMatcher.group(2));
        keyMap = pickUpInvalidArgs(keyMap);

        if (keyMap.size() != 0)
            throw new SelectorInvalidException(keyMap);
    }


    private static Map<String, String> pickUpInvalidArgs(Map<String, String> keyMap)
    {
        Map<String, String> result = new HashMap<>();

        keyMap.entrySet().stream().parallel().filter(ent -> {
            for (String key: usableArgs)
                if (ent.getKey().equals(key))
                    return false;

            Matcher matcher = scorePattern.matcher(ent.getKey());

            return !matcher.matches();
        }).forEach(ent -> result.put(ent.getKey(), ent.getValue()));

        return result;
    }


    public static boolean isValidSelectorType(String token)
    {
        return tokenPattern.matcher(token).matches();
    }

    public static List<Entity> matchEntities(CommandSender sender,  String token, Class<? extends Entity> clazz) throws SelectorInvalidException, SelectorMalformedException
    {
        validateSelector(token);

        Matcher tokenMatcher = tokenPattern.matcher(token);
        if (!tokenMatcher.matches())
            return Collections.emptyList();

        Map<String, String> keyMap = getArgumentMap(tokenMatcher.group(2));

        if (!isEntityTypeValid(keyMap))
            return Collections.emptyList();

        String dist = tokenMatcher.group(1);

        Location blockPos = getBlockPosFromArguments(
                keyMap,
                sender instanceof Player ? ((Player) sender).getLocation(): new Location(Bukkit.getWorlds().get(0), 0, 0, 0));

        List<World> worlds = getWorlds(sender, keyMap);

        ArrayList<Entity> filtered = new ArrayList<>();

        for(World world: worlds)
        {
            if (world == null)
                continue;
            ArrayList<Predicate<Entity>> pres = new ArrayList<>();
            pres.addAll(getXpLevelPredicates(keyMap));
            pres.addAll(getTypePredicates(keyMap, dist));
            pres.addAll(getGamemodePredicates(keyMap));
            pres.addAll(getTeamPredicates(keyMap));
            pres.addAll(getScorePredicates(keyMap));
            pres.addAll(getNamePredicates(keyMap));
            pres.addAll(getRadiusPredicates(keyMap, blockPos));
            pres.addAll(getRotationsPredicates(keyMap));
            filtered.addAll(filterResults(keyMap, pres, dist, world, blockPos));
        }

        return getEntitiesFromPredicates(filtered, keyMap, sender, clazz, dist, blockPos);
    }


    private static List<Predicate<Entity>> getTypePredicates(Map<String, String> params, String type)
    {
        ArrayList<Predicate<Entity>> ents =  new ArrayList<>();

        String entType = params.get("type");

        if (entType == null)
            return ents;

        boolean mirrored = entType.startsWith("!");

        entType = mirrored ? entType.substring(1): entType;

        String finalEntType = entType;
        ents.add(new Predicate<Entity>()
        {
            @Override
            public boolean test(Entity input)
            {
                if (input instanceof Player)
                    return finalEntType.equalsIgnoreCase("player") != mirrored;
                else
                    return input.getName().equalsIgnoreCase(finalEntType) != mirrored;
            }

            @Override
            public boolean apply(Entity input)
            {
                return test(input);
            }
        });

        return ents;
    }


    private static List<Entity> getEntitiesFromPredicates(List<Entity> match, Map<String, String> params, CommandSender sender, Class<? extends Entity> clazz, String type, Location blockPos)
    {
        int count = convertStringToInt(params.get("c"), !type.equals("a") && !type.equals("e") ? 1: 0);

        if (!type.equals("p") && !type.equals("a") && !type.equals("e"))
        {
            if (type.equals("r"))
                Collections.shuffle(match);
        }
        else if (blockPos != null)
        {
            match.sort((o1, o2) -> {
                Location locO1 = o1.getLocation().clone();
                Location locO2 = o2.getLocation().clone();
                locO1.setWorld(blockPos.getWorld());
                locO2.setWorld(blockPos.getWorld());
                return ComparisonChain.start().compare(locO1.distanceSquared(blockPos), locO2.distanceSquared(blockPos)).result();
            });
        }


        if (sender instanceof Player)
        {
            Entity commandSenderEntity = ((Player) sender);
            if (clazz.isAssignableFrom(commandSenderEntity.getClass()) && count == 1 && match.contains(commandSenderEntity) && !type.equals("r"))
                match = new ArrayList<>(Collections.singletonList(commandSenderEntity));
        }

        if (count != 0)
        {
            if (count < 0)
                Collections.reverse(match);
            match = match.subList(0, Math.min(Math.abs(count), match.size()));
        }

        return match;
    }

    private static List<Entity> filterResults(Map<String, String> params, List<Predicate<Entity>> inputs, String typeKey, World worldInput, Location blockPos)
    {

        if (inputs.size() == 0)
            inputs.add(a -> true);

        ArrayList<Entity> ents = new ArrayList<>();
        String type = params.get("type");
        type = type != null && type.startsWith("!") ? type.substring(1): type;
        boolean entityAll = !typeKey.equals("e");
        boolean random = typeKey.equals("r") && type != null;

        int dx = convertStringToInt(params.get("dx"), 0);
        int dy = convertStringToInt(params.get("dy"), 0);
        int dz = convertStringToInt(params.get("dz"), 0);
        int r = convertStringToInt(params.get("r"), -1);

        Predicate<Entity> predicate = Predicates.and(inputs);

        if (blockPos != null)
        {
            int players = worldInput.getPlayers().size();
            int entitiesIn = worldInput.getEntities().size();
            boolean nn = players < entitiesIn / 16;

            if(!params.containsKey("dx") && !params.containsKey("dy") && !params.containsKey("dz"))
            {
                if (r >= 0)
                {
                    if (entityAll && nn && !random)
                        ents.addAll(worldInput.getPlayers()
                                .stream().parallel()
                                .filter(player -> player.getLocation().distance(blockPos) <= r)
                                .collect(Collectors.toList()));
                    else
                        ents.addAll(worldInput.getNearbyEntities(blockPos, r, r, r)
                                .stream().parallel()
                                .filter(player -> player.getLocation().distance(blockPos) <= r)
                                .collect(Collectors.toList()));
                }
                else if (typeKey.equals("a"))
                    ents.addAll(worldInput.getPlayers().stream().filter(predicate::apply).collect(Collectors.toList()));
                else if (!typeKey.equals("p") && (!typeKey.equals("r") || random))
                    ents.addAll(worldInput.getEntities().stream().filter(predicate::apply).collect(Collectors.toList()));
                else
                    ents.addAll(worldInput.getPlayers().stream().filter(predicate::apply).collect(Collectors.toList()));
            }
            else
            {
                if (entityAll && nn && !random)
                    ents.addAll(worldInput.getNearbyEntities(blockPos, dx, dy, dz).stream().filter(predicate::apply).collect(Collectors.toList()));
                else
                    ents.addAll(worldInput.getNearbyEntities(blockPos, dx, dy, dz).stream().parallel().filter(entity -> entity instanceof Player).collect(Collectors.toList()));
            }
        }
        else if (typeKey.equals("a"))
            ents.addAll(worldInput.getPlayers().stream().filter(predicate::apply).collect(Collectors.toList()));
        else if (!typeKey.equals("p") && !typeKey.equals("r") || random)
            ents.addAll(worldInput.getEntities().stream().filter(predicate::apply).collect(Collectors.toList()));
        else
            ents.addAll(worldInput.getEntities().stream().filter(predicate::apply).collect(Collectors.toList()));
        return ents;
    }

    private static List<Predicate<Entity>> getRotationsPredicates(Map<String, String> params)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();
        if (params.containsKey("rym") || params.containsKey("ry"))
        {
            int rym = normalize(convertStringToInt(params.get("rym"), 0));
            int ry = normalize(convertStringToInt(params.get("ry"), 359));

            ents.add(new Predicate<Entity>()
            {
                @Override
                public boolean test(Entity input)
                {
                    int var2x = normalize((int)Math.floor((input.getLocation().getYaw())));
                    return rym > ry ? var2x >= rym || var2x <= ry: var2x >= rym && var2x <= ry;
                }

                @Override
                public boolean apply(Entity input)
                {
                    return test(input);
                }
            });
        }

        if (params.containsKey("rxm") || params.containsKey("rx"))
        {

            int rxm = normalize(convertStringToInt(params.get("rxm"), 0));
            int rx = normalize(convertStringToInt(params.get("rx"), 359));

            ents.add(new Predicate<Entity>()
            {
                @Override
                public boolean test(Entity input)
                {
                    int var2x = normalize((int)Math.floor((input.getLocation().getYaw())));
                    return rxm > rx ? var2x >= rxm || var2x <= rx: var2x >= rxm && var2x <= rx;
                }

                @Override
                public boolean apply(Entity input)
                {
                    return test(input);
                }
            });
        }

        return ents;
    }

    private static int normalize(int yaw)
    {
        yaw %= 360;

        if(yaw >= 160)
            yaw -= 360;

        if (yaw < 0)
            yaw += 360;

        return yaw;
    }

    private static List<Predicate<Entity>> getRadiusPredicates(Map<String, String> params, Location pos)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();
        int rm = convertStringToInt(params.get("rm"), -1);
        int r = convertStringToInt(params.get("r"), -1);

        if (pos == null)
            return ents;
        if (rm >= 0 || r >= 0)
        {
            int distanceCentralRm = rm * rm;
            int distanceCentralR = r * r;
            ents.add(new Predicate<Entity>()
            {
                @Override
                public boolean test(Entity input)
                {
                    if (input == null)
                        return false;

                    int distance = (int)input.getLocation().distanceSquared(pos);
                    return (rm < 0 || distance >= distanceCentralRm) &&
                            (r < 0 || distance <= distanceCentralR);
                }

                @Override
                public boolean apply(Entity input)
                {
                    return test(input);
                }
            });
        }

        return ents;
    }

    private static List<Predicate<Entity>> getNamePredicates(Map<String, String> params)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();

        String name = params.get("name");

        if (name == null)
            return ents;

        boolean c = name.startsWith("!");

        name = c ? name.substring(1): name;


        String finalName = name;
        ents.add(new Predicate<Entity>()
        {
            @Override
            public boolean test(Entity input)
            {
                if (input == null)
                    return false;
                return input.getName().equals(finalName) != c;
            }

            @Override
            public boolean apply(Entity input)
            {
                return test(input);
            }
        });

        return ents;
    }

    private static List<Predicate<Entity>> getScorePredicates(Map<String, String> params)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();

        final Map<String, Integer> scoreMap = getScoreMap(params);

        if (scoreMap.size() < 1)
            return ents;

        ents.add(new Predicate<Entity>()
        {
            @Override
            public boolean test(Entity input)
            {
                Scoreboard mainScore = Bukkit.getScoreboardManager().getMainScoreboard();

                Iterator<Map.Entry<String, Integer>> iterator = scoreMap.entrySet().iterator();

                Map.Entry<String, Integer> entry;
                boolean minMode;
                int bs;
                do
                {
                    if (!iterator.hasNext())
                        return true;

                    entry = iterator.next();
                    String name = entry.getKey();
                    minMode = false;

                    if (name.endsWith("_min") && name.length() > 4)
                    {
                        minMode = true;
                        name = name.substring(0, name.length() - 4);
                    }

                    Objective objective = mainScore.getObjective(name);

                    if (objective == null)
                        return false;

                    String keyName = input instanceof Player ? input.getName(): input.getUniqueId().toString();


                    Score score;

                    try
                    {
                        score = objective.getScore(keyName);
                    }
                    catch (Exception ignored) { return false; }

                    bs = score.getScore();
                    if (bs < entry.getValue() && minMode)
                        return false;

                }
                while(bs < entry.getValue() || minMode);

                return true;
            }

            @Override
            public boolean apply(Entity input)
            {
                return test(input);
            }
        });

        return ents;
    }

    private static Map<String, Integer> getScoreMap(Map<String, String> params)
    {
        HashMap<String, Integer> responses = new HashMap<>();

        for( String key: params.keySet())
        {
            if (!key.startsWith("score_"))
                continue;
            if (key.length() > 6)
                responses.put(key.substring(6), convertStringToInt(params.get(key), 1));
        }

        return responses;
    }

    private static List<Predicate<Entity>> getTeamPredicates(Map<String, String> params)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();
        String team = params.get("team");
        if (team == null)
            return ents;

        final boolean gf = team.startsWith("!");
        if (gf)
            team = team.substring(1);

        String finalTeam = team;
        ents.add(new Predicate<Entity>()
        {
            @Override
            public boolean test(Entity input)
            {
                if (!(input instanceof LivingEntity))
                    return false;

                if (Bukkit.getScoreboardManager() == null)
                    return false;

                String key = input.getUniqueId().toString();
                if (input instanceof Player)
                    key = input.getName();

                for (Team bukkitTeam: Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
                    if (bukkitTeam.hasEntry(key))
                        return bukkitTeam.getName().equals(finalTeam) != gf;


                return false;
            }

            @Override
            public boolean apply(Entity input)
            {
                return test(input);
            }
        });

        return ents;
    }

    @SuppressWarnings("deprecation")
    private static List<Predicate<Entity>> getGamemodePredicates(Map<String, String> params)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();
        final int value = convertStringToInt(params.get("m"), Bukkit.getDefaultGameMode().getValue());

        if (value == Bukkit.getDefaultGameMode().getValue())
            return ents;

        ents.add(new Predicate<Entity>()
        {
            @Override
            public boolean test(Entity input)
            {
                if (!(input instanceof Player))
                    return false;
                return ((Player) input).getGameMode().getValue() == value;
            }

            @Override
            public boolean apply(Entity input)
            {
                return test(input);
            }
        });

        return ents;
    }

    private static List<Predicate<Entity>> getXpLevelPredicates(Map<String, String> params)
    {
        ArrayList<Predicate<Entity>> ents = new ArrayList<>();
        final int lm = convertStringToInt(params.get("lm"), -1);
        final int l = convertStringToInt(params.get("l"), -1);
        if (lm <= -1 && l <= -1)
            return ents;

        ents.add(new Predicate<Entity>()
        {
            @Override
            public boolean test(Entity input)
            {
                if (!(input instanceof Player))
                    return false;
                Player player = (Player) input;

                return (lm <= -1 || player.getExpToLevel() >= lm) &&
                        (l <= -1 || player.getExpToLevel() <= l);
            }

            @Override
            public boolean apply(Entity input)
            {
                return test(input);
            }
        });

        return ents;
    }

    private static List<World> getWorlds(CommandSender sender,  Map<String, String> argumentMap)
    {
        if (hasArgument(argumentMap) && sender instanceof Player)
            return new ArrayList<World>(){{add(((Player) sender).getWorld());}};
        else
            return Bukkit.getWorlds();
    }

    private static int convertStringToInt(String string, int def)
    {
        if (string == null)
            return def;
        try
        {
            return Integer.parseInt(string);
        }
        catch (Exception ignored)
        {
            return def;
        }
    }

    private static boolean hasArgument(Map<String, String> params)
    {
        Iterator<String> iterator = Arrays.stream(worldBindingArgs).iterator();

        String key;
        do
        {
            if (!iterator.hasNext())
                return false;

            key = iterator.next();
        }
        while(!params.containsKey(key));

        return true;
    }

    private static Location getBlockPosFromArguments(Map<String, String> map,  Location defaultLoc)
    {
        Location location = defaultLoc.clone().getBlock().getLocation();

        if (map.containsKey("x") && canConvertDouble(map.get("x")))
            location.setX(NumberConversions.floor(Double.parseDouble(map.get("x"))));

        if (map.containsKey("y") && canConvertDouble(map.get("y")))
            location.setY(NumberConversions.floor(Double.parseDouble(map.get("y"))));

        if (map.containsKey("z") && canConvertDouble(map.get("z")))
            location.setZ(NumberConversions.floor(Double.parseDouble(map.get("z"))));

        return location;
    }

    private static boolean canConvertDouble(String str)
    {
        try
        {
            Double.parseDouble(str);
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private static boolean isEntityTypeValid(String name)
    {

        try
        {
            EntityType.valueOf(name.toUpperCase());
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private static boolean isEntityTypeValid(Map<String,String> params)
    {
        String type = params.get("type");
        if(type == null)
            return true;
        type = type.startsWith("!") ? type.substring(1): type;

        try
        {
            EntityType.valueOf(type.toUpperCase());
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private static Map<String, String> getArgumentMap(String arg)
    {
        HashMap<String, String> resultMap = new HashMap<>();
        if (arg == null)
            return resultMap;
        int count = 0;
        int buffer = -1;

        for(Matcher matcher = intListPattern.matcher(arg); matcher.find(); buffer = matcher.end())
        {
            String key;
            switch (count++)
            {
                case 0:
                    key = "x";
                    break;
                case 1:
                    key = "y";
                    break;
                case 2:
                    key = "z";
                    break;
                case 3:
                    key = "r";
                    break;
                default:
                    key = null;
            }

            if (key != null && matcher.group(1).length() > 0)
                resultMap.put(key, matcher.group(1));
        }

        if (buffer < arg.length())
        {
            Matcher keyList = keyValueListPattern.matcher(
                    buffer == -1 ? arg: arg.substring(buffer)
            );

            while(keyList.find())
                resultMap.put(keyList.group(1), keyList.group(2));
        }

        return resultMap;
    }
}
