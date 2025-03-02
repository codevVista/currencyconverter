package com.dexwin.currencyconventer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.dexwin.currencyconventer.databinding.FragmentFirstBinding;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private TextInputEditText amountInput;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private Button convertButton;
    private TextView resultText;

    private ExchangeRateApi exchangeRateApi;

    private String fromCurrencySpinnerItem_selected;
    private String toCurrencySpinnerItem_selected;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        // Initialize UI components

        amountInput = binding.amountInput;
        fromCurrencySpinner =binding.fromCurrencySpinner;
        toCurrencySpinner = binding.toCurrencySpinner;
        convertButton = binding.convertButton;
        resultText = binding.resultText;
        // Set up Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.exchangerate.host/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        exchangeRateApi = retrofit.create(ExchangeRateApi.class);

        // Set up spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.currencies,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);

        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCurrencySpinnerItem_selected=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(requireContext(), "Select Currency to convert from", Toast.LENGTH_SHORT).show();

            }
        });
        toCurrencySpinner.setPrompt("Select To Currency");

        toCurrencySpinner.setAdapter(adapter);

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCurrencySpinnerItem_selected=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(requireContext(), "Select Currency to convert to", Toast.LENGTH_SHORT).show();

            }
        });

        return binding.getRoot();

    }
    private void convertCurrency() {
       // String fromCurrency = fromCurrencySpinner.getText().toString();
        //String toCurrency = toCurrencySpinner.getText().toString();
        String amountString = amountInput.getText().toString();

        if (fromCurrencySpinnerItem_selected.isEmpty() || toCurrencySpinnerItem_selected.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountString);

        // Fetch exchange rate from API
        Call<ExchangeRateResponse> call = exchangeRateApi.getExchangeRate(fromCurrencySpinnerItem_selected, toCurrencySpinnerItem_selected, amount,"967b76bc315087476810dcc70423865f");
        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double result = response.body().getResult();
                    resultText.setText(String.format("%.2f %s", result, toCurrencySpinnerItem_selected));
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch exchange rate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convertCurrency();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}