package bin.mt.plugin.gemini;

import android.content.SharedPreferences;
import android.text.InputType;

import java.util.regex.Pattern;

import bin.mt.plugin.api.PluginContext;
import bin.mt.plugin.api.preference.PluginPreference;

/**
 * Preference screen for configuring OpenAI access within the plugin.
 *
 * Provides a dedicated page so users can manage their OpenAI API key,
 * pick a default model, and review documentation/pricing links without
 * cluttering the Gemini settings page.
 */
public class OpenRouterTranslatePreference implements PluginPreference {

    private PluginContext context;

    @Override
    public void onBuild(PluginContext context, Builder builder) {
        this.context = context;
        SharedPreferences preferences = context.getPreferences();

        builder.title(context.getString("{pref_openrouter_title}"))
                .subtitle(context.getString("{pref_openrouter_subtitle}"));

        // Overview
        builder.addText("{pref_openrouter_header_overview}").summary("");
        builder.addText("{pref_openrouter_overview_title}")
                .summary("{pref_openrouter_overview_summary}");
        builder.addText("{pref_openrouter_limits}")
                .summary("{pref_openrouter_limits_summary}");

        // API key configuration
        builder.addText("{pref_openrouter_header_api}").summary("");
        builder.addInput("{pref_openrouter_api_key_title}", GeminiConstants.PREF_OPENROUTER_API_KEY)
                .defaultValue(GeminiConstants.DEFAULT_API_KEY)
                .summary("{pref_openrouter_api_key_summary}")
                .valueAsSummary()
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.addText("{pref_openrouter_status_title}")
                .summary(describeKeyStatus(
                        preferences.getString(GeminiConstants.PREF_OPENROUTER_API_KEY, "")));

        builder.addText("{pref_openrouter_validate}")
                .summary("{pref_openrouter_validate_summary}")
                .onClick((pluginUI, item) -> validateApiKey());

        builder.addText("{pref_openrouter_get_key}")
                .summary("{pref_openrouter_get_key_summary}")
                .url(GeminiConstants.URL_OPENROUTER_KEYS);

        // Model and endpoint
        builder.addText("{pref_openrouter_header_model}").summary("");
        builder.addList("{pref_openrouter_model_title}", GeminiConstants.PREF_OPENROUTER_MODEL)
                .summary("{pref_openrouter_model_summary}")
                .addItem("⭐ Gemini 2.5 Flash", "google/gemini-2.5-flash")
                .addItem("Gemini 2.5 Pro", "google/gemini-2.5-pro")
                .addItem("Llama 3.3 70B", "meta-llama/llama-3.3-70b-instruct")
                .addItem("DeepSeek V3", "deepseek/deepseek-chat");

        builder.addInput("{pref_openrouter_endpoint_title}", GeminiConstants.PREF_OPENROUTER_ENDPOINT)
                .defaultValue(GeminiConstants.DEFAULT_OPENROUTER_ENDPOINT)
                .summary("{pref_openrouter_endpoint_summary}")
                .valueAsSummary();

        // Helpful resources
        builder.addText("{pref_openrouter_header_docs}").summary("");
        builder.addText("{pref_openrouter_docs}")
                .summary("{pref_openrouter_docs_summary}")
                .url(GeminiConstants.URL_OPENROUTER_DOCS);

        builder.addText("{pref_openrouter_pricing}")
                .summary("{pref_openrouter_pricing_summary}")
                .url(GeminiConstants.URL_OPENROUTER_PRICING);
    }

    private void validateApiKey() {
        SharedPreferences prefs = context.getPreferences();
        String apiKey = prefs.getString(GeminiConstants.PREF_OPENROUTER_API_KEY, "");
        if (apiKey != null) apiKey = apiKey.trim();
        if (apiKey == null || apiKey.isEmpty()) {
            context.showToast(context.getString("{error_openrouter_no_api_key}"));
            return;
        }

        if (isValidApiKey(apiKey)) {
            context.showToast(context.getString("{msg_openrouter_key_valid_format}"));
        } else {
            context.showToast(context.getString("{error_openrouter_invalid_key_format}"));
        }
    }

    private boolean isValidApiKey(String apiKey) {
        return apiKey != null && Pattern.matches(GeminiConstants.OPENROUTER_API_KEY_PATTERN, apiKey.trim());
    }

    private String describeKeyStatus(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return context.getString("{pref_status_missing}");
        }
        if (!isValidApiKey(apiKey)) {
            return context.getString("{pref_status_invalid_format}");
        }
        return context.getString("{pref_status_ready}");
    }
}
