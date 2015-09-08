package com.yandex.money.api.typeadapters.showcase.uicontrol;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.yandex.money.api.methods.JsonUtils;
import com.yandex.money.api.model.showcase.components.Component;
import com.yandex.money.api.model.showcase.components.container.Group;
import com.yandex.money.api.model.showcase.components.uicontrol.Select;
import com.yandex.money.api.typeadapters.showcase.ComponentsTypeProvider;

/**
 * Type adapter for {@link @Select} component.
 *
 * @author Anton Ermak (ermak@yamoney.ru)
 */
public final class SelectTypeAdapter extends ParameterControlTypeAdapter<Select, Select.Builder> {

    private static final SelectTypeAdapter INSTANCE = new SelectTypeAdapter();

    private static final String MEMBER_OPTIONS = "options";
    private static final String MEMBER_LABEL = "label";
    private static final String MEMBER_VALUE = "value";
    private static final String MEMBER_STYLE = "style";
    private static final String MEMBER_GROUP = "group";

    private SelectTypeAdapter() {
    }

    /**
     * @return instance of this class
     */
    public static SelectTypeAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    protected void deserialize(JsonObject src, Select.Builder builder,
                               JsonDeserializationContext context) {
        for (JsonElement item : src.getAsJsonArray(MEMBER_OPTIONS)) {
            JsonObject itemObject = item.getAsJsonObject();

            Select.Option option = new Select.Option(itemObject.get(MEMBER_LABEL).getAsString(),
                    itemObject.get(MEMBER_VALUE).getAsString());
            if (itemObject.has(MEMBER_GROUP)) {
                option.group = deserializeOptionGroup(itemObject.get(MEMBER_GROUP).getAsJsonArray(),
                        context);
            }
            builder.addOption(option);
        }
        builder.setStyle(Select.Style.parse(JsonUtils.getString(src, MEMBER_STYLE)));
        super.deserialize(src, builder, context);
    }

    @Override
    protected void serialize(Select src, JsonObject to, JsonSerializationContext context) {
        Select.Option selectedOption = src.getSelectedOption();

        if (selectedOption != null) {
            to.addProperty(MEMBER_VALUE, selectedOption.value);
        }
        if (src.style != null) {
            to.addProperty(MEMBER_STYLE, src.style.code);
        }

        JsonArray options = new JsonArray();
        for (Select.Option option : src.options) {
            JsonObject optionElement = new JsonObject();

            optionElement.addProperty(MEMBER_LABEL, option.label);
            optionElement.addProperty(MEMBER_VALUE, option.value);

            if (option.group != null) {
                optionElement.add(MEMBER_GROUP, serializeOptionGroup(option.group, context));
            }
            options.add(optionElement);
        }
        to.add(MEMBER_OPTIONS, options);
        super.serialize(src, to, context);
    }

    @Override
    protected Select.Builder createBuilderInstance() {
        return new Select.Builder();
    }

    @Override
    protected Select createInstance(Select.Builder builder) {
        return builder.create();
    }

    @Override
    protected Class<Select> getType() {
        return Select.class;
    }

    private static JsonArray serializeOptionGroup(Group group, JsonSerializationContext context) {
        JsonArray items = new JsonArray();
        for (Component item : group.items) {
            items.add(context.serialize(item));
        }
        return items;
    }

    private static Group deserializeOptionGroup(JsonArray items, JsonDeserializationContext
            context) {
        Group.Builder groupBuilder = new Group.Builder();
        groupBuilder.setLayout(Group.Layout.VERTICAL);
        for (JsonElement item : items) {
            Component component = context.deserialize(item,
                    ComponentsTypeProvider.getJsonComponentType(item));
            groupBuilder.addItem(component);
        }
        return groupBuilder.create();
    }
}
