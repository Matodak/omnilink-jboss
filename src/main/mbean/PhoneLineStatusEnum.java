package mbean;

import net.homeip.mleclerc.omnilink.enumeration.Enum;
import net.homeip.mleclerc.omnilink.enumeration.EnumInfo;

public class PhoneLineStatusEnum extends Enum
{
    public final static EnumInfo metaInfo = new EnumInfo();

    public final static PhoneLineStatusEnum UNKNOWN = new PhoneLineStatusEnum("Unknown", 0);
    public final static PhoneLineStatusEnum DEAD = new PhoneLineStatusEnum("Phone Line Dead", 8);
    public final static PhoneLineStatusEnum RING = new PhoneLineStatusEnum("Phone Line Ring", 9);
    public final static PhoneLineStatusEnum OFF_HOOK = new PhoneLineStatusEnum("Phone Line Off Hook", 10);
    public final static PhoneLineStatusEnum ON_HOOK = new PhoneLineStatusEnum("Phone Line On Hook", 11);

    public PhoneLineStatusEnum(String userLabel, int value)
    {
        super(userLabel, value);
        metaInfo.add(this);
    }
}
