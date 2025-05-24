package com.example.mtc_app.customer.fragments;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mtc_app.R;
import com.example.mtc_app.customer.CustomerHomePageActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MakeOrderFragment extends Fragment {


    // CheckBox Declarations

    // Main Categories
    private CheckBox cbCementCube, cbPowerBlock, cbSteel, cbBrick, cbSoil, cbAacBlock, cbConstWater, cbWasteWater, cbMixDesign;
    private CheckBox cbAggregateFine, cbAggregateCoarse, cbCement, cbNDT, cbFlyAsh, cbBitumen;

    // Aggregate Fine
    private CheckBox finenessModulusBygradationFine, siltContentFine, specificGravityAndWaterAbsorptionFine, soundnessFine, alkaliReactivityFine;

    // Aggregate Coarse
    private CheckBox cbImpactValueCoarse, SpecificGravityAndWaterAbsorptionCoarse, cbCrushingValueCoarse, cbSoundnessCyclesCoarse;
    private CheckBox cbFlakinessIndexAndElongationIndexCoarse, cbGradingOfAggregateCoarse, cbAbrasionValueCoarse, cbAlkaliReactivityCoarse;

    // Paver Block
    private CheckBox cbCompressiveStrengthPaver, cbWaterAbsorptionPaver;

    // Cement
    private CheckBox cbFinessByBlainCement, cbInitialSettingTimeCement, cbConsistencyCement, cbCompressiveCement;
    private CheckBox cbFinenessCement, cbSoundenessCemenet, cbCompressiveStrengthMortarCement, cbChemicalAnalysisCement;

    // Steel
    private CheckBox cbUnitWaitSteel, cbEnsileTestYieldAndElogationTestSteel, cbBendTestSteel, cbRebendTestSteel, cbChemicalAnalysisSteel;

    // Brick
    private CheckBox cbWaterAbsorptionBrick, cbDimensionTestBrick, cbCompressiveStrengthBrick, cbEfflorescenceBrick;

    TextInputEditText etFinalPrice;
    // Soil
    private CheckBox cbCBRTestUnsoakedSoil, cbGrainSizeAnalysisSoil, cbTestSoakedSoil, cbPlasticLimitSoil;
    private CheckBox cbLightCompactionTestSoil, cbHeavyCompactionTestSoil, cbFreeSwellIndexSoil, cbUnconfinedCompressionSoil;
    private CheckBox cbTriaxialTestUUSoil, cbTriaxialTestCUSoil, cbSwellingPressureSoil, cbSpecificGravitySoil, cbShrinkageLimitSoil;
    private CheckBox cbDirectShearSoil, cbPermeabilityTestSoil, cbRelativeDensitySoil, cbFieldDensityAndMoistureContentSoil, cbConsolidationSoil;

    // AAC & NDT
    private CheckBox cbMeasurementOfDimensionsAac, cbCompressiveStrengthAac, cbBlocksDensityAac, cbWaterAbsorptionAac;
    private CheckBox cbDryingShrinkageAac, cbMoistureMovementAac, cbUltrasonicPulseVelocityNDT, cbReboundHammerTestNDT;

    // Cement Conc. Cube
    private CheckBox cbCompressiveStrengthOfCube, cbCastingPreparingCubesOfGivenMixCube, cbFlexurerStrengthOfBeamCube, cbCastingPreparingBeamCube;

    // Const. Water
    private CheckBox cbSulphatesSO4CWater, cbAlkalinityCWater, cbPHValueCWater, cbOrganicImpuritiesCWater;
    private CheckBox cbInorganicImpuritiesCWater, cbChlorideAsClCWater, cbSuspendedMatterCWater, cbTDSTotalDissolvedSolidsCWater;

    // Waste Water
    private CheckBox cbChlorideAsClWasteWater, cbPhValueWasteWater, cbSulphatesSo4WasteWater;
    private CheckBox cbTdsTotalDissolvedSolidsWasteWater, cbTssTotalSuspendedSolidsWasteWater;

    // Concrete Mix Design
    private CheckBox cbWithCubeMixDesign, cbWithFlexurerStrengthMixDesign, cbWithAdmixtureMixDesign;

    // Fly Ash
    private CheckBox cbSpecificGravityFlyAsh, cbSoundnessFlyAsh, cbCompressiveStrengthFlyAsh;

    // Bitumen
    private CheckBox cbPenetrationValueBitumen, cbSofteningPointBitumen, cbDuctilityBitumen, cbSpecificGravityBitumen;

    // Quantity Input Fields
    private TextInputLayout tilPowerBlockQuantity, tilSteelQuantity, tillCemenetCubeQuantity, tilBrickQuantity, tilSoilQuantity, tilAacBlockQuantity;
    private TextInputLayout tilAggregateFineQuantity, tilAggregateCoarseQuantity, tilCementQuantity, tillConstWaterQuantity, tilWasteWaterQuantity;
    private TextInputLayout tillFlyAsh, tilMixDesignQuantity, tilBitumenQuantity, tilNDTQuantity;

    // Price Calculation
    private final Map<CheckBox, Integer> priceMap = new HashMap<>();
    private TextView tvTotalPrice;
    private int totalPrice = 0;

    // Customer & Dispatch Details
    private EditText etCustomerName, etDispatchAddress, etMobile, etEmail, etLab, etLabJob;
//    private EditText termsAndConditionsField;

    // Dispatch Mode
    private RadioGroup modeOfDispatchGroup;
    private RadioButton rbPost, rbCourier, rbSealed, rbOpen, rbReqYes, rbResYes, rbSampleSealed;

    // Sample Condition & Compliance
    private RadioGroup sampleConditionGroup, complianceStatementGroup, standardDeviationGroup;

    // Review Remarks
    private RadioGroup radioGroupRemark1, radioGroupRemark2, radioGroupRemark3;

    // Additional Inputs
    private CheckBox cbOnemethedOfTEsting, cbTwoTestingService, cbThreeTermsAndCondition;
    private TextInputEditText deviationInput, discussionInput;

    // Firestore Instance
    private FirebaseFirestore db;

    // State Management
    private boolean isSubmitting = false;

    // Submit Button
    private Button submitButton;

    // LinearLayout container for checkboxes
    private LinearLayout pointsGroup;



    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_make_order_fragment, container, false);

        pointsGroup = view.findViewById(R.id.pointsGroup);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        etCustomerName = view.findViewById(R.id.customer_name);
        etDispatchAddress = view.findViewById(R.id.dispatch_address); // Ensure this exists in XML
        etMobile = view.findViewById(R.id.mobile_number);
        etEmail = view.findViewById(R.id.email);
//        termsAndConditionsField = view.findViewById(R.id.terms_and_conditions);
        modeOfDispatchGroup = view.findViewById(R.id.mode_of_dispatch_group);
        sampleConditionGroup = view.findViewById(R.id.sample_condition_group);
        complianceStatementGroup = view.findViewById(R.id.compliance_statement_group);
        standardDeviationGroup = view.findViewById(R.id.standard_deviation_group);
        etLab = view.findViewById(R.id.lab_no);
        etLabJob = view.findViewById(R.id.lab_job_no);

        cbOnemethedOfTEsting = view.findViewById(R.id.OnemethedOfTEsting);
        cbTwoTestingService = view.findViewById(R.id.TwoTestingService);
        cbThreeTermsAndCondition = view.findViewById(R.id.ThreeTermsAndCondition);

        deviationInput = view.findViewById(R.id.deviation_input);
        discussionInput = view.findViewById(R.id.discussion_input);

        radioGroupRemark1 = view.findViewById(R.id.radio_group_remark1);
        radioGroupRemark2 = view.findViewById(R.id.radio_group_remark2);
        radioGroupRemark3 = view.findViewById(R.id.radio_group_remark3);


        // Add the ProgressBar
        ProgressBar loadingProgress = view.findViewById(R.id.loading_progress);

        submitButton = view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> {
            if (!isSubmitting) {
                submitData(view);
            }
        });

        // Initialize CheckBoxes
        cbPowerBlock = view.findViewById(R.id.cb_power_block);
        cbSteel = view.findViewById(R.id.cb_steel);
        cbConstWater = view.findViewById(R.id.cb_const_water);
        cbWasteWater = view.findViewById(R.id.cb_waste_water);
        cbFlyAsh = view.findViewById(R.id.cb_fly_ash);
        cbMixDesign = view.findViewById(R.id.cb_mix_design);
        cbBrick = view.findViewById(R.id.cb_brick);
        cbSoil = view.findViewById(R.id.cb_soil);
        cbCementCube = view.findViewById(R.id.cb_cement_cube);
        cbAacBlock = view.findViewById(R.id.cb_aac_block);
        cbAggregateFine = view.findViewById(R.id.cb_aggregate_fine);
        cbAggregateCoarse = view.findViewById(R.id.cb_aggregate_coarse);
        cbCement = view.findViewById(R.id.cb_cement);
        cbBitumen = view.findViewById(R.id.cb_bitumen);
        cbNDT = view.findViewById(R.id.cb_ndt);

        // Initialize Aggregate-Fine CheckBoxes
        finenessModulusBygradationFine = view.findViewById(R.id.AGGREGATE_fineness_modulus_bygradation);
        siltContentFine = view.findViewById(R.id.AGGREGATE_silt_content);
        specificGravityAndWaterAbsorptionFine = view.findViewById(R.id.AGGREGATE_specific_gravity_and_water_absorption);
        soundnessFine = view.findViewById(R.id.AGGREGATE_soundness);
        alkaliReactivityFine = view.findViewById(R.id.AGGREGATE_alkali_reactivity);
        cbSoundenessCemenet = view.findViewById(R.id.CEMENT_soundness);

        // Initialize AGGREGATE (COARSE) related checkboxes
        cbGradingOfAggregateCoarse = view.findViewById(R.id.AGGREGATE_grading_of_aggregate);
        cbFlakinessIndexAndElongationIndexCoarse = view.findViewById(R.id.AGGREGATE_flakiness_index_and_elongation_index);
        SpecificGravityAndWaterAbsorptionCoarse = view.findViewById(R.id.AGGREGATE_specific_gravity_and_water_absorption_coarse);
        cbImpactValueCoarse = view.findViewById(R.id.AGGREGATE_impact_value_coarse);
        cbAbrasionValueCoarse = view.findViewById(R.id.AGGREGATE_abrasion_value_coarse);
        cbCrushingValueCoarse = view.findViewById(R.id.AGGREGATE_crushing_value_coarse);
        cbSoundnessCyclesCoarse = view.findViewById(R.id.AGGREGATE_soundness_cycles_coarse);
        etFinalPrice = view.findViewById(R.id.et_final_price);
        etFinalPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateDisplayedTotalPriceFromManualField();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



        tvTotalPrice = view.findViewById(R.id.tv_total_price);

        cbAlkaliReactivityCoarse = view.findViewById(R.id.AGGREGATE_alkali_reactivity_coarse);

        // Initialize Paver Block checkboxes
        cbCompressiveStrengthPaver = view.findViewById(R.id.PAVER_compressive_strength);
        cbWaterAbsorptionPaver = view.findViewById(R.id.PAVER_water_absorption);

        // Initialize TextInputLayouts for All Quantity
        tilPowerBlockQuantity = view.findViewById(R.id.til_power_block_quantity);
        tilSteelQuantity = view.findViewById(R.id.til_steel_quantity);
        tillConstWaterQuantity = view.findViewById(R.id.til_const_water_quantity);
        tilBrickQuantity = view.findViewById(R.id.til_brick_quantity);
        tilSoilQuantity = view.findViewById(R.id.til_soil_quantity);
        tilAacBlockQuantity = view.findViewById(R.id.til_aac_block_quantity);
        tillCemenetCubeQuantity = view.findViewById(R.id.til_cement_cube_quantity);
        tilAggregateFineQuantity = view.findViewById(R.id.til_aggregate_fine_quantity);
        tilAggregateCoarseQuantity = view.findViewById(R.id.til_aggregate_coarse_quantity);
        tilCementQuantity = view.findViewById(R.id.til_cement_quantity);
        tilWasteWaterQuantity = view.findViewById(R.id.til_waste_water_quantity);
        tillFlyAsh = view.findViewById(R.id.til_fly_ash_quantity);
        tilMixDesignQuantity = view.findViewById(R.id.til_mix_water_quantity);
        tilBitumenQuantity = view.findViewById(R.id.til_bitumen_quantity);
        tilNDTQuantity = view.findViewById(R.id.til_ndt_quantity);

        // Initialize Cement checkbox
        cbConsistencyCement = view.findViewById(R.id.CEMENT_consistency);
        cbInitialSettingTimeCement = view.findViewById(R.id.CEMENT_initial_setting_time);
        cbFinessByBlainCement = view.findViewById(R.id.CEMENT_finess_by_blain);
        cbFinenessCement = view.findViewById(R.id.CEMENT_fineness);
        cbCompressiveCement = view.findViewById(R.id.CEMENT_compressive);
        cbSoundenessCemenet = view.findViewById(R.id.CEMENT_soundness);
        cbCompressiveStrengthMortarCement = view.findViewById(R.id.CEMENT_COMPRESSIVE_STRENGTH_MORTAR);
        cbChemicalAnalysisCement = view.findViewById(R.id.cement_chemical_analysis);

        // Initialize Steel checkbox
        cbUnitWaitSteel = view.findViewById(R.id.STEEL_unit_wait);
        cbEnsileTestYieldAndElogationTestSteel = view.findViewById(R.id.STEEL_ensile_test_yield_and_elogation_test);
        cbBendTestSteel = view.findViewById(R.id.STEEL_bend_test);
        cbRebendTestSteel = view.findViewById(R.id.STEEL_rebend_test);
        cbChemicalAnalysisSteel = view.findViewById(R.id.STEEL_chemical_analysis);

        // Initialize Brick checkbox
        cbCompressiveStrengthBrick = view.findViewById(R.id.BRICK_compressive_strength);
        cbDimensionTestBrick = view.findViewById(R.id.BRICK_dimension_test);
        cbWaterAbsorptionBrick = view.findViewById(R.id.BRICK_Fine_water_absorption);
        cbEfflorescenceBrick = view.findViewById(R.id.BRICK_efflorescence);

        // Initialize Soil checkbox and its related checkboxes
        cbCBRTestUnsoakedSoil = view.findViewById(R.id.SOIL_cbr_test_unsoaked);
        cbGrainSizeAnalysisSoil = view.findViewById(R.id.SOIL_grain_size_analysis);
        cbTestSoakedSoil = view.findViewById(R.id.SOIL_cbr_test_soaked);
        cbPlasticLimitSoil = view.findViewById(R.id.SOIL_plastic_limit);
        cbLightCompactionTestSoil = view.findViewById(R.id.SOIL_light_compaction_test);
        cbHeavyCompactionTestSoil = view.findViewById(R.id.SOIL_heavy_compaction_test);
        cbFreeSwellIndexSoil = view.findViewById(R.id.SOIL_free_swell_index);
        cbShrinkageLimitSoil = view.findViewById(R.id.SOIL_shrinkage_limit);
        cbDirectShearSoil = view.findViewById(R.id.SOIL_direct_shear);
        cbPermeabilityTestSoil = view.findViewById(R.id.SOIL_permeability_test);
        cbRelativeDensitySoil = view.findViewById(R.id.SOIL_relative_density);
        cbFieldDensityAndMoistureContentSoil = view.findViewById(R.id.SOIL_field_density_and_moisture_content);
        cbConsolidationSoil = view.findViewById(R.id.SOIL_consolidation);
        cbUnconfinedCompressionSoil = view.findViewById(R.id.SOIL_unconfined_compression);
        cbTriaxialTestUUSoil = view.findViewById(R.id.SOIL_triaxial_test_uu);
        cbTriaxialTestCUSoil = view.findViewById(R.id.SOIL_triaxial_test_cu);
        cbSwellingPressureSoil = view.findViewById(R.id.SOIL_swelling_pressure);
        cbSpecificGravitySoil = view.findViewById(R.id.SOIL_specific_gravity);

        // Initialize AAC checkboxes
        cbMeasurementOfDimensionsAac = view.findViewById(R.id.AAC_measurement_of_dimensions);
        cbCompressiveStrengthAac = view.findViewById(R.id.AAC_compressive_strength);
        cbBlocksDensityAac = view.findViewById(R.id.AAC_blocks_density);
        cbWaterAbsorptionAac = view.findViewById(R.id.AAC_water_absorption);
        cbDryingShrinkageAac = view.findViewById(R.id.AAC_drying_shrinkage);
        cbMoistureMovementAac = view.findViewById(R.id.AAC_moisture_movement);

        // Initialize Cement Cube checkboxes
        cbCompressiveStrengthOfCube = view.findViewById(R.id.CUBE_compressive_strength_of_cube);
        cbCastingPreparingCubesOfGivenMixCube = view.findViewById(R.id.CUBE_casting_preparing_cubes_of_given_mix);
        cbFlexurerStrengthOfBeamCube = view.findViewById(R.id.CUBE_flexurer_strength_of_beam);
        cbCastingPreparingBeamCube = view.findViewById(R.id.CUBE_casting_preparing_beam);

        // Initialize Constant Water checkbox
        cbSulphatesSO4CWater = view.findViewById(R.id.C_WATER_sulphates_so4);
        cbAlkalinityCWater = view.findViewById(R.id.C_WATER_alkalinity);
        cbPHValueCWater = view.findViewById(R.id.C_WATER_ph_value);
        cbOrganicImpuritiesCWater = view.findViewById(R.id.C_WATER_organic_impurities);
        cbInorganicImpuritiesCWater = view.findViewById(R.id.C_WATER_inorganic_impurities);
        cbChlorideAsClCWater = view.findViewById(R.id.C_WATER_chloride_as_cl);
        cbSuspendedMatterCWater = view.findViewById(R.id.C_WATER_suspended_matter);
        cbTDSTotalDissolvedSolidsCWater = view.findViewById(R.id.C_WATER_tds_total_dissolved_solids);

        // Initialize Waste Water checkbox
        cbChlorideAsClWasteWater = view.findViewById(R.id.WASTE_WATER_chloride_as_cl);
        cbPhValueWasteWater = view.findViewById(R.id.WASTE_WATER_ph_value);
        cbSulphatesSo4WasteWater = view.findViewById(R.id.WASTE_WATER_sulphates_so4);
        cbTdsTotalDissolvedSolidsWasteWater = view.findViewById(R.id.WASTE_WATER_tds_total_dissolved_solids);
        cbTssTotalSuspendedSolidsWasteWater = view.findViewById(R.id.WASTE_WATER_tss_total_suspended_solids);

        // Initialize Concrete Mix Design checkbox
        cbWithCubeMixDesign = view.findViewById(R.id.MIX_DESIGN_with_cube);
        cbWithFlexurerStrengthMixDesign = view.findViewById(R.id.MIX_DESIGN_with_flexurer_strength);
        cbWithAdmixtureMixDesign = view.findViewById(R.id.MIX_DESIGN_with_admixture);

        // Initialize Fly Ash checkbox
        cbSpecificGravityFlyAsh = view.findViewById(R.id.FLY_ASH_specific_gravity);
        cbSoundnessFlyAsh = view.findViewById(R.id.FLY_ASH_soundness);
        cbCompressiveStrengthFlyAsh = view.findViewById(R.id.FLY_ASH_compressive_strength);

        // Bitumen checkboxes
        cbPenetrationValueBitumen = view.findViewById(R.id.BITUMEN_penetration_value);
        cbSofteningPointBitumen = view.findViewById(R.id.BITUMEN_softening_point);
        cbDuctilityBitumen = view.findViewById(R.id.BITUMEN_ductility);
        cbSpecificGravityBitumen = view.findViewById(R.id.BITUMEN_specific_gravity);

        // Initialize NDT checkboxes
        cbUltrasonicPulseVelocityNDT = view.findViewById(R.id.NDT_ultrasonic_pulse_velocity);
        cbReboundHammerTestNDT = view.findViewById(R.id.NDT_rebound_hammer_test);

        SharedPreferences prefs = getContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        String savedName = prefs.getString("name", "");
        String savedEmail = prefs.getString("email", "");
        String savedAddress = prefs.getString("address", "");
        String savedPhone = prefs.getString("phone", "");

        etCustomerName.setText(savedName);
        etEmail.setText(savedEmail);
        etDispatchAddress.setText(savedAddress);
        etMobile.setText(savedPhone);

        // Set up for All checkboxes
        setUpCheckboxListener(cbPowerBlock, tilPowerBlockQuantity);
        setUpCheckboxListener(cbSteel, tilSteelQuantity);
        setUpCheckboxListener(cbBrick, tilBrickQuantity);
        setUpCheckboxListener(cbSoil, tilSoilQuantity);
        setUpCheckboxListener(cbAacBlock, tilAacBlockQuantity);
        setUpCheckboxListener(cbCementCube, tillCemenetCubeQuantity);
        setUpCheckboxListener(cbAggregateCoarse, tilAggregateCoarseQuantity);
        setUpCheckboxListener(cbCement, tilCementQuantity);
        setUpCheckboxListener(cbConstWater, tillConstWaterQuantity);
        setUpCheckboxListener(cbWasteWater, tilWasteWaterQuantity);
        setUpCheckboxListener(cbMixDesign, tilMixDesignQuantity);
        setUpCheckboxListener(cbFlyAsh, tillFlyAsh);
        setUpCheckboxListener(cbNDT, tilNDTQuantity);

        //price Aggregate Fine listeners
        setupPriceChangeListener(finenessModulusBygradationFine, 250);
        setupPriceChangeListener(siltContentFine, 200);
        setupPriceChangeListener(specificGravityAndWaterAbsorptionFine, 350);
        setupPriceChangeListener(soundnessFine, 1200);
        setupPriceChangeListener(alkaliReactivityFine, 1500);

        //price Aggregate Coarse listeners
        setupPriceChangeListener(cbGradingOfAggregateCoarse, 250);
        setupPriceChangeListener(cbFlakinessIndexAndElongationIndexCoarse, 350);
        setupPriceChangeListener(SpecificGravityAndWaterAbsorptionCoarse, 350);
        setupPriceChangeListener(cbCrushingValueCoarse, 350);
        setupPriceChangeListener(cbSoundnessCyclesCoarse, 1200);
        setupPriceChangeListener(cbAbrasionValueCoarse, 350);
        setupPriceChangeListener(cbImpactValueCoarse, 250);
        setupPriceChangeListener(cbAlkaliReactivityCoarse, 1500);

        //price Power Block listeners
        setupPriceChangeListener(cbWaterAbsorptionPaver, 200);
        setupPriceChangeListener(cbCompressiveStrengthPaver, 700);

        //price Cement listeners
        setupPriceChangeListener(cbConsistencyCement, 300);
        setupPriceChangeListener(cbInitialSettingTimeCement, 400);
        setupPriceChangeListener(cbFinessByBlainCement, 600);
        setupPriceChangeListener(cbFinenessCement, 250);
        setupPriceChangeListener(cbCompressiveCement, 550);
        setupPriceChangeListener(cbSoundenessCemenet, 250);
        setupPriceChangeListener(cbCompressiveStrengthMortarCement, 200);
        setupPriceChangeListener(cbChemicalAnalysisCement, 2250);


        //price Steel listeners
        setupPriceChangeListener(cbUnitWaitSteel, 50);
        setupPriceChangeListener(cbEnsileTestYieldAndElogationTestSteel, 400);
        setupPriceChangeListener(cbBendTestSteel, 150);
        setupPriceChangeListener(cbRebendTestSteel, 150);
        setupPriceChangeListener(cbChemicalAnalysisSteel, 900);

        //price Brick listeners
        setupPriceChangeListener(cbCompressiveStrengthBrick, 400);
        setupPriceChangeListener(cbDimensionTestBrick, 150);
        setupPriceChangeListener(cbWaterAbsorptionBrick, 250);
        setupPriceChangeListener(cbEfflorescenceBrick, 200);

        //price Soil listeners
        setupPriceChangeListener(cbCBRTestUnsoakedSoil, 600);
        setupPriceChangeListener(cbGrainSizeAnalysisSoil, 300);
        setupPriceChangeListener(cbTestSoakedSoil, 900);
        setupPriceChangeListener(cbPlasticLimitSoil, 350);
        setupPriceChangeListener(cbLightCompactionTestSoil, 600);
        setupPriceChangeListener(cbHeavyCompactionTestSoil, 900);
        setupPriceChangeListener(cbFreeSwellIndexSoil, 150);
        setupPriceChangeListener(cbShrinkageLimitSoil, 300);
        setupPriceChangeListener(cbDirectShearSoil, 700);
        setupPriceChangeListener(cbPermeabilityTestSoil, 900);
        setupPriceChangeListener(cbRelativeDensitySoil, 200);
        setupPriceChangeListener(cbFieldDensityAndMoistureContentSoil, 600);
        setupPriceChangeListener(cbConsolidationSoil, 1200);
        setupPriceChangeListener(cbUnconfinedCompressionSoil, 300);
        setupPriceChangeListener(cbTriaxialTestUUSoil, 900);
        setupPriceChangeListener(cbTriaxialTestCUSoil, 1500);
        setupPriceChangeListener(cbSpecificGravitySoil, 200);
        setupPriceChangeListener(cbSwellingPressureSoil, 900);

        //price AAC listeners
        setupPriceChangeListener(cbMeasurementOfDimensionsAac, 1200);
        setupPriceChangeListener(cbCompressiveStrengthAac, 1200);
        setupPriceChangeListener(cbBlocksDensityAac, 1200);
        setupPriceChangeListener(cbWaterAbsorptionAac, 1200);
        setupPriceChangeListener(cbDryingShrinkageAac, 350);
        setupPriceChangeListener(cbMoistureMovementAac, 200);

        //price Const Water listeners
        setupPriceChangeListener(cbSulphatesSO4CWater, 900);
        setupPriceChangeListener(cbAlkalinityCWater, 900);
        setupPriceChangeListener(cbPHValueCWater, 900);
        setupPriceChangeListener(cbOrganicImpuritiesCWater, 900);
        setupPriceChangeListener(cbInorganicImpuritiesCWater, 900);
        setupPriceChangeListener(cbChlorideAsClCWater, 900);
        setupPriceChangeListener(cbSuspendedMatterCWater, 900);
        setupPriceChangeListener(cbTDSTotalDissolvedSolidsCWater, 900);

        //price Waste Water listeners
        setupPriceChangeListener(cbChlorideAsClWasteWater, 700);
        setupPriceChangeListener(cbPhValueWasteWater, 700);
        setupPriceChangeListener(cbSulphatesSo4WasteWater, 700);
        setupPriceChangeListener(cbTdsTotalDissolvedSolidsWasteWater, 700);
        setupPriceChangeListener(cbTssTotalSuspendedSolidsWasteWater, 700);

        //price Concrete Mix Design listeners
        setupPriceChangeListener(cbWithCubeMixDesign, 6000);
        setupPriceChangeListener(cbWithFlexurerStrengthMixDesign, 7500);
        setupPriceChangeListener(cbWithAdmixtureMixDesign, 9000);

        //price Cement Cube listeners
        setupPriceChangeListener(cbCompressiveStrengthOfCube, 400);
        setupPriceChangeListener(cbCastingPreparingCubesOfGivenMixCube, 1000);
        setupPriceChangeListener(cbFlexurerStrengthOfBeamCube, 450);
        setupPriceChangeListener(cbCastingPreparingBeamCube, 900);

        //price Fly Ash listeners
        setupPriceChangeListener(cbSpecificGravityFlyAsh, 200);
        setupPriceChangeListener(cbSoundnessFlyAsh, 250);
        setupPriceChangeListener(cbCompressiveStrengthFlyAsh, 550);

        //price Bitumen listeners
        setupPriceChangeListener(cbPenetrationValueBitumen, 1100);
        setupPriceChangeListener(cbSofteningPointBitumen, 1500);
        setupPriceChangeListener(cbDuctilityBitumen, 1050);
        setupPriceChangeListener(cbSpecificGravityBitumen, 350);

        //price NDT listeners
        setupPriceChangeListener(cbUltrasonicPulseVelocityNDT, 300);
        setupPriceChangeListener(cbReboundHammerTestNDT, 300);


        // Special setup for Aggregate Fine checkbox
        cbAggregateFine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the Aggregate Fine quantity input and other checkboxes
                tilAggregateFineQuantity.setVisibility(View.VISIBLE);
                finenessModulusBygradationFine.setVisibility(View.VISIBLE);
                siltContentFine.setVisibility(View.VISIBLE);
                specificGravityAndWaterAbsorptionFine.setVisibility(View.VISIBLE);
                soundnessFine.setVisibility(View.VISIBLE);
                alkaliReactivityFine.setVisibility(View.VISIBLE);


            } else {
                // Hide the Aggregate Fine quantity input and other checkboxes
                tilAggregateFineQuantity.setVisibility(View.GONE);
                finenessModulusBygradationFine.setVisibility(View.GONE);
                siltContentFine.setVisibility(View.GONE);
                specificGravityAndWaterAbsorptionFine.setVisibility(View.GONE);
                soundnessFine.setVisibility(View.GONE);
                alkaliReactivityFine.setVisibility(View.GONE);

                // Uncheck the additional checkboxes when hiding them
                finenessModulusBygradationFine.setChecked(false);
                siltContentFine.setChecked(false);
                specificGravityAndWaterAbsorptionFine.setChecked(false);
                soundnessFine.setChecked(false);
                alkaliReactivityFine.setChecked(false);
            }
        });

        // Special setup for Aggregate Coarse checkbox
        cbAggregateCoarse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all the related checkboxes
                tilAggregateCoarseQuantity.setVisibility(View.VISIBLE);
                cbGradingOfAggregateCoarse.setVisibility(View.VISIBLE);
                cbFlakinessIndexAndElongationIndexCoarse.setVisibility(View.VISIBLE);
                SpecificGravityAndWaterAbsorptionCoarse.setVisibility(View.VISIBLE);
                cbImpactValueCoarse.setVisibility(View.VISIBLE);
                cbAbrasionValueCoarse.setVisibility(View.VISIBLE);
                cbCrushingValueCoarse.setVisibility(View.VISIBLE);
                cbSoundnessCyclesCoarse.setVisibility(View.VISIBLE);
                cbAlkaliReactivityCoarse.setVisibility(View.VISIBLE);
            } else {
                // Hide all the related checkboxes and uncheck them
                tilAggregateCoarseQuantity.setVisibility(View.GONE);
                cbGradingOfAggregateCoarse.setVisibility(View.GONE);
                cbFlakinessIndexAndElongationIndexCoarse.setVisibility(View.GONE);
                SpecificGravityAndWaterAbsorptionCoarse.setVisibility(View.GONE);
                cbImpactValueCoarse.setVisibility(View.GONE);
                cbAbrasionValueCoarse.setVisibility(View.GONE);
                cbCrushingValueCoarse.setVisibility(View.GONE);
                cbSoundnessCyclesCoarse.setVisibility(View.GONE);
                cbAlkaliReactivityCoarse.setVisibility(View.GONE);

                cbGradingOfAggregateCoarse.setChecked(false);
                cbFlakinessIndexAndElongationIndexCoarse.setChecked(false);
                SpecificGravityAndWaterAbsorptionCoarse.setChecked(false);
                cbImpactValueCoarse.setChecked(false);
                cbAbrasionValueCoarse.setChecked(false);
                cbCrushingValueCoarse.setChecked(false);
                cbSoundnessCyclesCoarse.setChecked(false);
                cbAlkaliReactivityCoarse.setChecked(false);
            }
        });


        // Set up listener for PAVER checkbox
        cbPowerBlock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the related checkboxes
                tilPowerBlockQuantity.setVisibility(View.VISIBLE);
                cbCompressiveStrengthPaver.setVisibility(View.VISIBLE);
                cbWaterAbsorptionPaver.setVisibility(View.VISIBLE);
            } else {
                // Hide the related checkboxes and uncheck them
                tilPowerBlockQuantity.setVisibility(View.GONE);
                cbCompressiveStrengthPaver.setVisibility(View.GONE);
                cbWaterAbsorptionPaver.setVisibility(View.GONE);

                cbCompressiveStrengthPaver.setChecked(false);
                cbWaterAbsorptionPaver.setChecked(false);
            }
        });

        // Set up listener for the Cement checkbox
        cbCement.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all related checkboxes when cbCement is checked
                tilCementQuantity.setVisibility(View.VISIBLE);
                cbConsistencyCement.setVisibility(View.VISIBLE);
                cbInitialSettingTimeCement.setVisibility(View.VISIBLE);
                cbFinessByBlainCement.setVisibility(View.VISIBLE);
                cbFinenessCement.setVisibility(View.VISIBLE);
                cbCompressiveCement.setVisibility(View.VISIBLE);
                cbSoundenessCemenet.setVisibility(View.VISIBLE);
                cbCompressiveStrengthMortarCement.setVisibility(View.VISIBLE);
                cbChemicalAnalysisCement.setVisibility(View.VISIBLE);
            } else {
                // Hide all related checkboxes when cbCement is unchecked
                tilCementQuantity.setVisibility(View.GONE);
                cbConsistencyCement.setVisibility(View.GONE);
                cbInitialSettingTimeCement.setVisibility(View.GONE);
                cbFinessByBlainCement.setVisibility(View.GONE);
                cbFinenessCement.setVisibility(View.GONE);
                cbCompressiveCement.setVisibility(View.GONE);
                cbSoundenessCemenet.setVisibility(View.GONE);
                cbCompressiveStrengthMortarCement.setVisibility(View.GONE);
                cbChemicalAnalysisCement.setVisibility(View.GONE);

                // Uncheck them as well
                cbConsistencyCement.setChecked(false);
                cbInitialSettingTimeCement.setChecked(false);
                cbFinessByBlainCement.setChecked(false);
                cbFinenessCement.setChecked(false);
                cbCompressiveCement.setChecked(false);
                cbSoundenessCemenet.setChecked(false);
                cbCompressiveStrengthMortarCement.setChecked(false);
                cbChemicalAnalysisCement.setChecked(false);
            }
        });

        // Set up listener for the Steel checkbox
        cbSteel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the Compressive Strength checkbox when cbConcrete is checked
                tilSteelQuantity.setVisibility(View.VISIBLE);
                cbUnitWaitSteel.setVisibility(View.VISIBLE);
                cbEnsileTestYieldAndElogationTestSteel.setVisibility(View.VISIBLE);
                cbBendTestSteel.setVisibility(View.VISIBLE);
                cbRebendTestSteel.setVisibility(View.VISIBLE);
                cbChemicalAnalysisSteel.setVisibility(View.VISIBLE);

            } else {
                // Hide the Compressive Strength checkbox when cbConcrete is unchecked
                tilSteelQuantity.setVisibility(View.GONE);
                cbUnitWaitSteel.setVisibility(View.GONE);
                cbEnsileTestYieldAndElogationTestSteel.setVisibility(View.GONE);
                cbBendTestSteel.setVisibility(View.GONE);
                cbRebendTestSteel.setVisibility(View.GONE);
                cbChemicalAnalysisSteel.setVisibility(View.GONE);


                // Uncheck it as well
                cbUnitWaitSteel.setChecked(false);
                cbEnsileTestYieldAndElogationTestSteel.setChecked(false);
                cbBendTestSteel.setChecked(false);
                cbRebendTestSteel.setChecked(false);
                cbChemicalAnalysisSteel.setChecked(false);

            }
        });

        // Set up listener for Brick checkbox
        cbBrick.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Brick-related checkboxes when cbBrick is checked
                tilBrickQuantity.setVisibility(View.VISIBLE);
                cbCompressiveStrengthBrick.setVisibility(View.VISIBLE);
                cbDimensionTestBrick.setVisibility(View.VISIBLE);
                cbWaterAbsorptionBrick.setVisibility(View.VISIBLE);
                cbEfflorescenceBrick.setVisibility(View.VISIBLE);
            } else {
                // Hide all Brick-related checkboxes when cbBrick is unchecked
                tilBrickQuantity.setVisibility(View.GONE);
                cbCompressiveStrengthBrick.setVisibility(View.GONE);
                cbDimensionTestBrick.setVisibility(View.GONE);
                cbWaterAbsorptionBrick.setVisibility(View.GONE);
                cbEfflorescenceBrick.setVisibility(View.GONE);

                // Uncheck all related checkboxes
                cbCompressiveStrengthBrick.setChecked(false);
                cbDimensionTestBrick.setChecked(false);
                cbWaterAbsorptionBrick.setChecked(false);
                cbEfflorescenceBrick.setChecked(false);
            }
        });

        // Set up listener for the Soil checkbox
        cbSoil.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Soil-related checkboxes when cbSoil is checked
                tilSoilQuantity.setVisibility(View.VISIBLE);
                cbCBRTestUnsoakedSoil.setVisibility(View.VISIBLE);
                cbGrainSizeAnalysisSoil.setVisibility(View.VISIBLE);
                cbTestSoakedSoil.setVisibility(View.VISIBLE);
                cbPlasticLimitSoil.setVisibility(View.VISIBLE);
                cbLightCompactionTestSoil.setVisibility(View.VISIBLE);
                cbHeavyCompactionTestSoil.setVisibility(View.VISIBLE);
                cbFreeSwellIndexSoil.setVisibility(View.VISIBLE);
                cbShrinkageLimitSoil.setVisibility(View.VISIBLE);
                cbDirectShearSoil.setVisibility(View.VISIBLE);
                cbPermeabilityTestSoil.setVisibility(View.VISIBLE);
                cbRelativeDensitySoil.setVisibility(View.VISIBLE);
                cbFieldDensityAndMoistureContentSoil.setVisibility(View.VISIBLE);
                cbConsolidationSoil.setVisibility(View.VISIBLE);
                cbUnconfinedCompressionSoil.setVisibility(View.VISIBLE);
                cbTriaxialTestUUSoil.setVisibility(View.VISIBLE);
                cbTriaxialTestCUSoil.setVisibility(View.VISIBLE);
                cbSwellingPressureSoil.setVisibility(View.VISIBLE);
                cbSpecificGravitySoil.setVisibility(View.VISIBLE);

            } else {
                // Hide all Soil-related checkboxes when cbSoil is unchecked
                tilSoilQuantity.setVisibility(View.GONE);
                cbCBRTestUnsoakedSoil.setVisibility(View.GONE);
                cbGrainSizeAnalysisSoil.setVisibility(View.GONE);
                cbTestSoakedSoil.setVisibility(View.GONE);
                cbPlasticLimitSoil.setVisibility(View.GONE);
                cbLightCompactionTestSoil.setVisibility(View.GONE);
                cbHeavyCompactionTestSoil.setVisibility(View.GONE);
                cbFreeSwellIndexSoil.setVisibility(View.GONE);
                cbShrinkageLimitSoil.setVisibility(View.GONE);
                cbDirectShearSoil.setVisibility(View.GONE);
                cbPermeabilityTestSoil.setVisibility(View.GONE);
                cbRelativeDensitySoil.setVisibility(View.GONE);
                cbFieldDensityAndMoistureContentSoil.setVisibility(View.GONE);
                cbConsolidationSoil.setVisibility(View.GONE);
                cbUnconfinedCompressionSoil.setVisibility(View.GONE);
                cbTriaxialTestUUSoil.setVisibility(View.GONE);
                cbTriaxialTestCUSoil.setVisibility(View.GONE);
                cbSwellingPressureSoil.setVisibility(View.GONE);
                cbSpecificGravitySoil.setVisibility(View.GONE);

                cbCBRTestUnsoakedSoil.setChecked(false);
                cbGrainSizeAnalysisSoil.setChecked(false);
                cbTestSoakedSoil.setChecked(false);
                cbPlasticLimitSoil.setChecked(false);
                cbLightCompactionTestSoil.setChecked(false);
                cbHeavyCompactionTestSoil.setChecked(false);
                cbFreeSwellIndexSoil.setChecked(false);
                cbShrinkageLimitSoil.setChecked(false);
                cbDirectShearSoil.setChecked(false);
                cbPermeabilityTestSoil.setChecked(false);
                cbRelativeDensitySoil.setChecked(false);
                cbFieldDensityAndMoistureContentSoil.setChecked(false);
                cbConsolidationSoil.setChecked(false);
                cbUnconfinedCompressionSoil.setChecked(false);
                cbTriaxialTestUUSoil.setChecked(false);
                cbTriaxialTestCUSoil.setChecked(false);
                cbSwellingPressureSoil.setChecked(false);
                cbSpecificGravitySoil.setChecked(false);
            }
        });

        // Set up listeners for the AAC checkboxes if needed
        cbAacBlock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Bar-related checkboxes when Ultimate Tensile Strength is checked
                tilAacBlockQuantity.setVisibility(View.VISIBLE);
                cbMeasurementOfDimensionsAac.setVisibility(View.VISIBLE);
                cbCompressiveStrengthAac.setVisibility(View.VISIBLE);
                cbBlocksDensityAac.setVisibility(View.VISIBLE);
                cbWaterAbsorptionAac.setVisibility(View.VISIBLE);
                cbDryingShrinkageAac.setVisibility(View.VISIBLE);
                cbMoistureMovementAac.setVisibility(View.VISIBLE);
            } else {
                // Hide all Bar-related checkboxes when it's unchecked
                tilAacBlockQuantity.setVisibility(View.GONE);
                cbMeasurementOfDimensionsAac.setVisibility(View.GONE);
                cbCompressiveStrengthAac.setVisibility(View.GONE);
                cbBlocksDensityAac.setVisibility(View.GONE);
                cbWaterAbsorptionAac.setVisibility(View.GONE);
                cbDryingShrinkageAac.setVisibility(View.GONE);
                cbMoistureMovementAac.setVisibility(View.GONE);

                cbMeasurementOfDimensionsAac.setChecked(false);
                cbCompressiveStrengthAac.setChecked(false);
                cbBlocksDensityAac.setChecked(false);
                cbWaterAbsorptionAac.setChecked(false);
                cbDryingShrinkageAac.setChecked(false);
                cbMoistureMovementAac.setChecked(false);
            }
        });

        // Set up listeners for the Cement Cube checkboxes if needed
        cbCementCube.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Bar-related checkboxes when Ultimate Tensile Strength is checked
                tillCemenetCubeQuantity.setVisibility(View.VISIBLE);
                cbCompressiveStrengthOfCube.setVisibility(View.VISIBLE);
                cbCastingPreparingCubesOfGivenMixCube.setVisibility(View.VISIBLE);
                cbFlexurerStrengthOfBeamCube.setVisibility(View.VISIBLE);
                cbCastingPreparingBeamCube.setVisibility(View.VISIBLE);

            } else {
                // Hide all Bar-related checkboxes when it's unchecked
                tillCemenetCubeQuantity.setVisibility(View.GONE);
                cbCompressiveStrengthOfCube.setVisibility(View.GONE);
                cbCastingPreparingCubesOfGivenMixCube.setVisibility(View.GONE);
                cbFlexurerStrengthOfBeamCube.setVisibility(View.GONE);
                cbCastingPreparingBeamCube.setVisibility(View.GONE);

                cbCompressiveStrengthOfCube.setChecked(false);
                cbCastingPreparingCubesOfGivenMixCube.setChecked(false);
                cbFlexurerStrengthOfBeamCube.setChecked(false);
                cbCastingPreparingBeamCube.setChecked(false);

            }
        });

        // Set up listeners for Const Water checkboxes
        cbConstWater.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Bar-related checkboxes when Ultimate Tensile Strength is checked
                tillConstWaterQuantity.setVisibility(View.VISIBLE);
                cbSulphatesSO4CWater.setVisibility(View.VISIBLE);
                cbAlkalinityCWater.setVisibility(View.VISIBLE);
                cbPHValueCWater.setVisibility(View.VISIBLE);
                cbOrganicImpuritiesCWater.setVisibility(View.VISIBLE);
                cbInorganicImpuritiesCWater.setVisibility(View.VISIBLE);
                cbChlorideAsClCWater.setVisibility(View.VISIBLE);
                cbSuspendedMatterCWater.setVisibility(View.VISIBLE);
                cbTDSTotalDissolvedSolidsCWater.setVisibility(View.VISIBLE);
            } else {
                // Hide all Bar-related checkboxes when it's unchecked
                tillConstWaterQuantity.setVisibility(View.GONE);
                cbSulphatesSO4CWater.setVisibility(View.GONE);
                cbAlkalinityCWater.setVisibility(View.GONE);
                cbPHValueCWater.setVisibility(View.GONE);
                cbOrganicImpuritiesCWater.setVisibility(View.GONE);
                cbInorganicImpuritiesCWater.setVisibility(View.GONE);
                cbChlorideAsClCWater.setVisibility(View.GONE);
                cbSuspendedMatterCWater.setVisibility(View.GONE);
                cbTDSTotalDissolvedSolidsCWater.setVisibility(View.GONE);

                cbSulphatesSO4CWater.setChecked(false);
                cbAlkalinityCWater.setChecked(false);
                cbPHValueCWater.setChecked(false);
                cbOrganicImpuritiesCWater.setChecked(false);
                cbInorganicImpuritiesCWater.setChecked(false);
                cbChlorideAsClCWater.setChecked(false);
                cbSuspendedMatterCWater.setChecked(false);
                cbTDSTotalDissolvedSolidsCWater.setChecked(false);
            }
        });

        // Set up listeners for Waste Water checkboxes
        cbWasteWater.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Bar-related checkboxes when Ultimate Tensile Strength is checked
                tilWasteWaterQuantity.setVisibility(View.VISIBLE);
                cbChlorideAsClWasteWater.setVisibility(View.VISIBLE);
                cbPhValueWasteWater.setVisibility(View.VISIBLE);
                cbSulphatesSo4WasteWater.setVisibility(View.VISIBLE);
                cbTdsTotalDissolvedSolidsWasteWater.setVisibility(View.VISIBLE);
                cbTssTotalSuspendedSolidsWasteWater.setVisibility(View.VISIBLE);

            } else {
                // Hide all Bar-related checkboxes when it's unchecked
                tilWasteWaterQuantity.setVisibility(View.GONE);
                cbChlorideAsClWasteWater.setVisibility(View.GONE);
                cbPhValueWasteWater.setVisibility(View.GONE);
                cbSulphatesSo4WasteWater.setVisibility(View.GONE);
                cbTdsTotalDissolvedSolidsWasteWater.setVisibility(View.GONE);
                cbTssTotalSuspendedSolidsWasteWater.setVisibility(View.GONE);

                cbChlorideAsClWasteWater.setChecked(false);
                cbPhValueWasteWater.setChecked(false);
                cbSulphatesSo4WasteWater.setChecked(false);
                cbTdsTotalDissolvedSolidsWasteWater.setChecked(false);
                cbTssTotalSuspendedSolidsWasteWater.setChecked(false);
            }
        });

        // Set up listeners for CONCRETE MIX DESIGN checkboxes
        cbMixDesign.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tilMixDesignQuantity.setVisibility(View.VISIBLE);
                cbWithCubeMixDesign.setVisibility(View.VISIBLE);
                cbWithFlexurerStrengthMixDesign.setVisibility(View.VISIBLE);
                cbWithAdmixtureMixDesign.setVisibility(View.VISIBLE);

            } else {
                tilMixDesignQuantity.setVisibility(View.GONE);
                cbWithCubeMixDesign.setVisibility(View.GONE);
                cbWithFlexurerStrengthMixDesign.setVisibility(View.GONE);
                cbWithAdmixtureMixDesign.setVisibility(View.GONE);

                cbWithCubeMixDesign.setChecked(false);
                cbWithFlexurerStrengthMixDesign.setChecked(false);
                cbWithAdmixtureMixDesign.setChecked(false);

            }
        });

        // Set up listeners for Fly Ash checkboxes
        cbFlyAsh.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Bar-related checkboxes when Ultimate Tensile Strength is checked
                tillFlyAsh.setVisibility(View.VISIBLE);
                cbSpecificGravityFlyAsh.setVisibility(View.VISIBLE);
                cbSoundnessFlyAsh.setVisibility(View.VISIBLE);
                cbCompressiveStrengthFlyAsh.setVisibility(View.VISIBLE);

            } else {
                // Hide all Bar-related checkboxes when it's unchecked
                tillFlyAsh.setVisibility(View.GONE);
                cbSpecificGravityFlyAsh.setVisibility(View.GONE);
                cbSoundnessFlyAsh.setVisibility(View.GONE);
                cbCompressiveStrengthFlyAsh.setVisibility(View.GONE);

                cbSpecificGravityFlyAsh.setChecked(false);
                cbSoundnessFlyAsh.setChecked(false);
                cbCompressiveStrengthFlyAsh.setChecked(false);
            }
        });

        // Set up listener for Bitumen checkbox
        cbBitumen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show all Bitumen-related checkboxes and quantity input when cbBitumen is checked
                tilBitumenQuantity.setVisibility(View.VISIBLE);
                cbPenetrationValueBitumen.setVisibility(View.VISIBLE);
                cbSofteningPointBitumen.setVisibility(View.VISIBLE);
                cbDuctilityBitumen.setVisibility(View.VISIBLE);
                cbSpecificGravityBitumen.setVisibility(View.VISIBLE);
            } else {
                // Hide all Bitumen-related checkboxes and quantity input when cbBitumen is unchecked
                tilBitumenQuantity.setVisibility(View.GONE);
                cbPenetrationValueBitumen.setVisibility(View.GONE);
                cbSofteningPointBitumen.setVisibility(View.GONE);
                cbDuctilityBitumen.setVisibility(View.GONE);
                cbSpecificGravityBitumen.setVisibility(View.GONE);

                cbPenetrationValueBitumen.setChecked(false);
                cbSofteningPointBitumen.setChecked(false);
                cbDuctilityBitumen.setChecked(false);
                cbSpecificGravityBitumen.setChecked(false);
            }
            calculateTotalPrice();
        });

        // Set up listener for NDT checkbox
        cbNDT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tilNDTQuantity.setVisibility(View.VISIBLE);
                cbUltrasonicPulseVelocityNDT.setVisibility(View.VISIBLE);
                cbReboundHammerTestNDT.setVisibility(View.VISIBLE);
            } else {
                tilNDTQuantity.setVisibility(View.GONE);
                cbUltrasonicPulseVelocityNDT.setVisibility(View.GONE);
                cbReboundHammerTestNDT.setVisibility(View.GONE);

                cbUltrasonicPulseVelocityNDT.setChecked(false);
                cbReboundHammerTestNDT.setChecked(false);
            }
        });

        // Initialize the view components
        TextInputLayout deviationInputLayout = view.findViewById(R.id.deviation_input_layout);
        TextView deviationText = view.findViewById(R.id.radio_text);

        // Initially hide the input field and the text
        deviationInputLayout.setVisibility(View.GONE);
        deviationText.setVisibility(View.GONE);

        // Set the listener for the RadioGroup to check for changes
        standardDeviationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check if the "YES" option is selected
                if (checkedId == R.id.radio_yes) {
                    // Show the TextInputLayout (TextField) and the associated label
                    deviationText.setVisibility(View.VISIBLE);
                    deviationInputLayout.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.radio_no) {
                    // Hide the TextInputLayout (TextField) and the associated label
                    deviationText.setVisibility(View.GONE);
                    deviationInputLayout.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private void updateDisplayedTotalPriceFromManualField() {
        String manualPriceStr = etFinalPrice.getText().toString().trim();
        if (!manualPriceStr.isEmpty()) {
            try {
                int manualValue = Integer.parseInt(manualPriceStr);
                tvTotalPrice.setText("Total Price: " + manualValue + " /- Rs");
            } catch (NumberFormatException ignored) {}
        } else {
            tvTotalPrice.setText("Total Price: " + totalPrice + " /- Rs"); // fallback to auto-calculated
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Hide the progress bar when the fragment is visible
        if (getActivity() != null && getActivity() instanceof CustomerHomePageActivity) {
            ((CustomerHomePageActivity) getActivity()).hideProgressBar();
        }
    }

    private void redirectToHome() {
        if (getActivity() == null) return;
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CustomerHomeFragment())
                .commit();
    }

    private void submitData(View view) {
        if (etCustomerName == null || etDispatchAddress == null || etLab == null || etLabJob == null) {
            Log.e("SubmitData", "EditText fields are null");
            return;
        }

        isSubmitting = true;
        submitButton.setEnabled(false);

        // Get current date
        String createdAt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Collect basic form data
        String customerName = etCustomerName.getText().toString().trim();
        String dispatchAddress = etDispatchAddress.getText().toString().trim();
        String mobileNumber = etMobile.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
//        String termsAndConditions = termsAndConditionsField.getText().toString().trim();
        String Lab = etLab.getText().toString().trim();
        String LabJob = etLabJob.getText().toString().trim();

//        etFinalPrice = view.findViewById(R.id.et_final_price);
//        etFinalPrice.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                calculateTotalPrice();  // Automatically update totalPrice display
//            }
//        });


        tvTotalPrice.setText("Total Price: " + totalPrice + " /- Rs");
        updateDisplayedTotalPriceFromManualField();





        // Validate inputs
        if (!validateInputs(customerName, dispatchAddress, mobileNumber, email, Lab, LabJob)) return;

        // Collect radio button selections
        Map<String, String> radioSelections = new HashMap<>();
        radioSelections.put("Mode of Dispatch", getSelectedRadioButtonText(modeOfDispatchGroup));
        radioSelections.put("Sample Condition", getSelectedRadioButtonText(sampleConditionGroup));
        radioSelections.put("Compliance Statement", getSelectedRadioButtonText(complianceStatementGroup));
        radioSelections.put("Standard Deviation", getSelectedRadioButtonText(standardDeviationGroup));

        // Collect checkboxes selections
        Map<String, Boolean> selectedPoints = new HashMap<>();
        selectedPoints.put("Method of Testing", cbOnemethedOfTEsting.isChecked());
        selectedPoints.put("Testing Services Approved", cbTwoTestingService.isChecked());
        selectedPoints.put("Terms & Conditions Accepted", cbThreeTermsAndCondition.isChecked());

        // Collect review remarks (only if selected)
        Map<String, String> reviewRemarks = new HashMap<>();
        if (radioGroupRemark1.getCheckedRadioButtonId() != -1) {
            reviewRemarks.put("Requirements Defined", getSelectedRadioButtonText(radioGroupRemark1));
        }
        if (radioGroupRemark2.getCheckedRadioButtonId() != -1) {
            reviewRemarks.put("Capability Available", getSelectedRadioButtonText(radioGroupRemark2));
        }
        if (radioGroupRemark3.getCheckedRadioButtonId() != -1) {
            reviewRemarks.put("Condition of Sample", getSelectedRadioButtonText(radioGroupRemark3));
        }

        // Collect deviation and discussion details
        String deviationDetails = deviationInput.getText().toString().trim();
        String discussionDetails = discussionInput.getText().toString().trim();

        // Collect segment-wise test selections (only if the main checkbox is checked)
        Map<String, List<String>> testSelections = new HashMap<>();

        // Aggregate Fine (Example logic for sub-checkbox storage)
        if (cbAggregateFine.isChecked()) {
            testSelections.put("Aggregate Fine", getSelectedTests(
                    finenessModulusBygradationFine, "WITH CUBE",
                    siltContentFine, "WITH FLEXURER STRENGTH",
                    specificGravityAndWaterAbsorptionFine, "WITH ADMIXTURE",
                    soundnessFine, "WITH FLEXURER STRENGTH",
                    alkaliReactivityFine, "WITH ADMIXTURE"
            ));
        }

        // Aggregate Coarse
        if (cbAggregateCoarse.isChecked()) {
            testSelections.put("Aggregate Coarse", getSelectedTests(
                    cbImpactValueCoarse, "IMPACT VALUE",
                    SpecificGravityAndWaterAbsorptionCoarse, "SPECIFIC GRAVITY AND WATER ABSORPTION",
                    cbCrushingValueCoarse, "CRUSHING VALUE",
                    cbSoundnessCyclesCoarse, "SOUNDNESS CYCLES",
                    cbFlakinessIndexAndElongationIndexCoarse, "FLAKINESS AND ELONGATION INDEX",
                    cbGradingOfAggregateCoarse, "GRADING OF AGGREGATE",
                    cbAbrasionValueCoarse, "ABRASION VALUE",
                    cbAlkaliReactivityCoarse, "ALKALI REACTIVITY"
            ));
        }

        // Cement
        if (cbCement.isChecked()) {
            testSelections.put("Cement", getSelectedTests(
                    cbFinessByBlainCement, "FINENESS BY BLAIN",
                    cbInitialSettingTimeCement, "INITIAL SETTING TIME",
                    cbConsistencyCement, "CONSISTENCY",
                    cbCompressiveCement, "COMPRESSIVE STRENGTH",
                    cbFinenessCement, "FINENESS",
                    cbSoundenessCemenet, "SOUNDNESS",
                    cbCompressiveStrengthMortarCement, "COMPRESSIVE STRENGTH OF MORTAR",
                    cbChemicalAnalysisCement, "CHEMICAL ANALYSIS"
            ));
        }

        // Steel
        if (cbSteel.isChecked()) {
            testSelections.put("Steel", getSelectedTests(
                    cbUnitWaitSteel, "UNIT WEIGHT",
                    cbEnsileTestYieldAndElogationTestSteel, "TENSILE TEST YIELD AND ELONGATION TEST",
                    cbBendTestSteel, "BEND TEST",
                    cbRebendTestSteel, "REBEND TEST",
                    cbChemicalAnalysisSteel, "CHEMICAL ANALYSIS"
            ));
        }

        // CementCube
        if (cbCementCube.isChecked()) {
            testSelections.put("CementCube", getSelectedTests(
                    cbCompressiveStrengthOfCube, "COMPRESSIVE STRENGTH OF CUBE",
                    cbCastingPreparingCubesOfGivenMixCube, "CASTING AND PREPARING CUBES OF GIVEN MIX",
                    cbFlexurerStrengthOfBeamCube, "FLEXURAL STRENGTH OF BEAM",
                    cbCastingPreparingBeamCube, "CASTING AND PREPARING BEAM"
            ));
        }

        // AAC Block
        if (cbAacBlock.isChecked()) {
            testSelections.put("AacBlock", getSelectedTests(
                    cbMeasurementOfDimensionsAac, "MEASUREMENT OF DIMENSIONS",
                    cbCompressiveStrengthAac, "COMPRESSIVE STRENGTH",
                    cbBlocksDensityAac, "BLOCKS DENSITY",
                    cbWaterAbsorptionAac, "WATER ABSORPTION",
                    cbDryingShrinkageAac, "DRYING SHRINKAGE",
                    cbMoistureMovementAac, "MOISTURE MOVEMENT"
            ));
        }

        // Power Block
        if (cbPowerBlock.isChecked()) {
            testSelections.put("PowerBlock", getSelectedTests(
                    cbWaterAbsorptionPaver, "WATER ABSORPTION",
                    cbCompressiveStrengthPaver, "COMPRESSIVE STRENGTH"
            ));
        }

        // Brick
        if (cbBrick.isChecked()) {
            testSelections.put("Brick", getSelectedTests(
                    cbCompressiveStrengthBrick, "COMPRESSIVE STRENGTH",
                    cbDimensionTestBrick, "DIMENSION TEST",
                    cbWaterAbsorptionBrick, "WATER ABSORPTION",
                    cbEfflorescenceBrick, "EFFLORESCENCE"
            ));
        }

        // Soil
        if (cbSoil.isChecked()) {
            testSelections.put("Soil", getSelectedTests(
                    cbCBRTestUnsoakedSoil, "CBR TEST UNSOAKED",
                    cbGrainSizeAnalysisSoil, "GRAIN SIZE ANALYSIS",
                    cbTestSoakedSoil, "TEST SOAKED",
                    cbPlasticLimitSoil, "PLASTIC LIMIT",
                    cbLightCompactionTestSoil, "LIGHT COMPACTION TEST",
                    cbHeavyCompactionTestSoil, "HEAVY COMPACTION TEST",
                    cbFreeSwellIndexSoil, "FREE SWELL INDEX",
                    cbShrinkageLimitSoil, "SHRINKAGE LIMIT",
                    cbDirectShearSoil, "DIRECT SHEAR TEST",
                    cbPermeabilityTestSoil, "PERMEABILITY TEST",
                    cbRelativeDensitySoil, "RELATIVE DENSITY",
                    cbFieldDensityAndMoistureContentSoil, "FIELD DENSITY AND MOISTURE CONTENT",
                    cbConsolidationSoil, "CONSOLIDATION TEST",
                    cbUnconfinedCompressionSoil, "UNCONFINED COMPRESSION TEST",
                    cbTriaxialTestUUSoil, "TRIAXIAL TEST UU",
                    cbTriaxialTestCUSoil, "TRIAXIAL TEST CU",
                    cbSpecificGravitySoil, "SPECIFIC GRAVITY",
                    cbSwellingPressureSoil, "SWELLING PRESSURE"
            ));
        }

        // NDT
        if (cbNDT.isChecked()) {
            testSelections.put("NDT", getSelectedTests(
                    cbUltrasonicPulseVelocityNDT, "ULTRASONIC PULSE VELOCITY",
                    cbReboundHammerTestNDT, "REBOUND HAMMER TEST"
            ));
        }

        // Construction Water
        if (cbConstWater.isChecked()) {
            testSelections.put("ConstWater", getSelectedTests(
                    cbSulphatesSO4CWater, "SULPHATES (SO4)",
                    cbAlkalinityCWater, "ALKALINITY",
                    cbPHValueCWater, "PH VALUE",
                    cbOrganicImpuritiesCWater, "ORGANIC IMPURITIES",
                    cbInorganicImpuritiesCWater, "INORGANIC IMPURITIES",
                    cbChlorideAsClCWater, "CHLORIDE AS CL",
                    cbSuspendedMatterCWater, "SUSPENDED MATTER",
                    cbTDSTotalDissolvedSolidsCWater, "TOTAL DISSOLVED SOLIDS (TDS)"
            ));
        }

        // Waste Water
        if (cbWasteWater.isChecked()) {
            testSelections.put("WasteWater", getSelectedTests(
                    cbChlorideAsClWasteWater, "CHLORIDE AS CL",
                    cbPhValueWasteWater, "PH VALUE",
                    cbSulphatesSo4WasteWater, "SULPHATES (SO4)",
                    cbTdsTotalDissolvedSolidsWasteWater, "TOTAL DISSOLVED SOLIDS (TDS)",
                    cbTssTotalSuspendedSolidsWasteWater, "TOTAL SUSPENDED SOLIDS (TSS)"
            ));
        }

        // Concrete Mix Design
        if (cbMixDesign.isChecked()) {
            testSelections.put("MixDesign", getSelectedTests(
                    cbWithCubeMixDesign, "WITH CUBE MIX DESIGN",
                    cbWithFlexurerStrengthMixDesign, "WITH FLEXURAL STRENGTH MIX DESIGN",
                    cbWithAdmixtureMixDesign, "WITH ADMIXTURE MIX DESIGN"
            ));
        }

        // Fly Ash
        if (cbFlyAsh.isChecked()) {
            testSelections.put("FlyAsh", getSelectedTests(
                    cbSpecificGravityFlyAsh, "SPECIFIC GRAVITY",
                    cbSoundnessFlyAsh, "SOUNDNESS",
                    cbCompressiveStrengthFlyAsh, "COMPRESSIVE STRENGTH"
            ));
        }

        // Bitumen
        if (cbBitumen.isChecked()) {
            testSelections.put("Bitumen", getSelectedTests(
                    cbPenetrationValueBitumen, "PENETRATION VALUE",
                    cbSofteningPointBitumen, "SOFTENING POINT",
                    cbDuctilityBitumen, "DUCTILITY",
                    cbSpecificGravityBitumen, "SPECIFIC GRAVITY"
            ));
        }

        // Prepare final data object
        Map<String, Object> data = new HashMap<>();
        data.put("Customer Name", customerName);
        data.put("Dispatch Address", dispatchAddress);
        data.put("Mobile Number", mobileNumber);
        data.put("Email", email);
//        data.put("Terms And Conditions", termsAndConditions);
        data.put("Created At", createdAt);

        String manualPriceStr = etFinalPrice.getText().toString().trim();
        if (!manualPriceStr.isEmpty()) totalPrice = Integer.parseInt(manualPriceStr);
        data.put("Total Price", totalPrice);


        data.put("Radio Selections", radioSelections);
        data.put("Selected Points", selectedPoints);
        data.put("LabNumber", Lab);
        data.put("LabJobNumber",LabJob);
        data.put("Status", "Pending"); // Automatically set status to "Pending"

        if (!reviewRemarks.isEmpty()) {
            data.put("Review Remarks", reviewRemarks);
        }

        data.put("Deviation Details", deviationDetails);
        data.put("Discussion Details", discussionDetails);

        if (!testSelections.isEmpty()) {
            data.put("Test Selections", testSelections);
        }

//        SharedPreferences.Editor editor = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE).edit();
        Context context = getContext();
        if (context != null) {
            if (getArguments() == null || getArguments().getString("customer_phone") == null) {
                // Only save if it’s not a customer-specific view
                SharedPreferences.Editor editor = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE).edit();
                editor.putString("name", customerName);
                editor.putString("email", email);
                editor.putString("address", dispatchAddress);
                editor.putString("phone", mobileNumber);
                editor.apply();
            }

        }




        // Store data in Firestore
        db.collection("Total Orders")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Order submitted successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    redirectToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    isSubmitting = false;
                    submitButton.setEnabled(true);
                });
    }
    private boolean validateInputs(String customerName, String dispatchAddress, String mobileNumber, String email, String lab, String labJob) {
        if (customerName.isEmpty()) {
            showError("Customer name is required");
            return false;
        }
        if (!customerName.matches("^[a-zA-Z ]+$")) {
            showError("Customer name must contain only letters");
            return false;
        }
        if (dispatchAddress.isEmpty()) {
            showError("Dispatch address is required");
            return false;
        }
        if (mobileNumber.isEmpty()) {
            showError("Mobile number is required");
            return false;
        }
        if (!mobileNumber.matches("\\d{10}")) {
            showError("Mobile number must be exactly 10 digits");
            return false;
        }
        if (email.isEmpty()) {
            showError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return false;
        }
        if (lab.isEmpty()) {
            showError("Lab number is required");
            return false;
        }
        if (labJob.isEmpty()) {
            showError("Lab job number is required");
            return false;
        }
        if (modeOfDispatchGroup.getCheckedRadioButtonId() == -1) {
            showError("Please select a Mode of Dispatch option");
            return false;
        }
        return true;
    }

    private List<String> getSelectedTests(Object... items) {
        List<String> selectedTests = new ArrayList<>();

        for (int i = 0; i < items.length; i += 2) {
            if (items[i] instanceof CheckBox && items[i + 1] instanceof String) {
                CheckBox checkBox = (CheckBox) items[i];
                String label = (String) items[i + 1];

                if (checkBox.isChecked()) {
                    selectedTests.add(label);
                }
            }
        }

        return selectedTests;
    }




    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        if (radioGroup == null) {
            return "";
        }
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
            if (selectedRadioButton != null) {
                return selectedRadioButton.getText().toString();
            }
        }
        return "";
    }

    private void clearForm() {
        etCustomerName.setText("");
        etDispatchAddress.setText("");
        etMobile.setText("");
        etEmail.setText("");
//        termsAndConditionsField.setText("");
        for (int i = 0; i < pointsGroup.getChildCount(); i++) {
            View child = pointsGroup.getChildAt(i);
            if (child instanceof CheckBox) {
                ((CheckBox) child).setChecked(false);
            }
        }
        sampleConditionGroup.clearCheck();
        complianceStatementGroup.clearCheck();
        standardDeviationGroup.clearCheck();
        etLab.setText("");
        etLabJob.setText("");

        //Aggregate Fine
        finenessModulusBygradationFine.setChecked(false);
        siltContentFine.setChecked(false);
        specificGravityAndWaterAbsorptionFine.setChecked(false);
        soundnessFine.setChecked(false);
        alkaliReactivityFine.setChecked(false);

        // Aggregate Coarse
        cbImpactValueCoarse.setChecked(false);
        SpecificGravityAndWaterAbsorptionCoarse.setChecked(false);
        cbCrushingValueCoarse.setChecked(false);
        cbSoundnessCyclesCoarse.setChecked(false);
        cbFlakinessIndexAndElongationIndexCoarse.setChecked(false);
        cbGradingOfAggregateCoarse.setChecked(false);
        cbAbrasionValueCoarse.setChecked(false);
        cbAlkaliReactivityCoarse.setChecked(false);

        // Steel
        cbUnitWaitSteel.setChecked(false);
        cbEnsileTestYieldAndElogationTestSteel.setChecked(false);
        cbBendTestSteel.setChecked(false);
        cbRebendTestSteel.setChecked(false);
        cbChemicalAnalysisSteel.setChecked(false);

        // Brick
        cbWaterAbsorptionBrick.setChecked(false);
        cbDimensionTestBrick.setChecked(false);
        cbCompressiveStrengthBrick.setChecked(false);
        cbEfflorescenceBrick.setChecked(false);

        // Soil
        cbCBRTestUnsoakedSoil.setChecked(false);
        cbGrainSizeAnalysisSoil.setChecked(false);
        cbTestSoakedSoil.setChecked(false);
        cbPlasticLimitSoil.setChecked(false);
        cbLightCompactionTestSoil.setChecked(false);
        cbHeavyCompactionTestSoil.setChecked(false);
        cbFreeSwellIndexSoil.setChecked(false);
        cbUnconfinedCompressionSoil.setChecked(false);
        cbTriaxialTestUUSoil.setChecked(false);
        cbTriaxialTestCUSoil.setChecked(false);
        cbSwellingPressureSoil.setChecked(false);
        cbSpecificGravitySoil.setChecked(false);
        cbShrinkageLimitSoil.setChecked(false);
        cbDirectShearSoil.setChecked(false);
        cbPermeabilityTestSoil.setChecked(false);
        cbRelativeDensitySoil.setChecked(false);
        cbFieldDensityAndMoistureContentSoil.setChecked(false);
        cbConsolidationSoil.setChecked(false);

        //Cement
        cbFinessByBlainCement.setChecked(false);
        cbInitialSettingTimeCement.setChecked(false);
        cbConsistencyCement.setChecked(false);
        cbCompressiveCement.setChecked(false);
        cbFinenessCement.setChecked(false);
        cbSoundenessCemenet.setChecked(false);
        cbCompressiveStrengthMortarCement.setChecked(false);
        cbChemicalAnalysisCement.setChecked(false);

        //Paver Block
        cbCompressiveStrengthPaver.setChecked(false);
        cbWaterAbsorptionPaver.setChecked(false);

        // AAC
        cbMeasurementOfDimensionsAac.setChecked(false);
        cbCompressiveStrengthAac.setChecked(false);
        cbBlocksDensityAac.setChecked(false);
        cbWaterAbsorptionAac.setChecked(false);
        cbDryingShrinkageAac.setChecked(false);
        cbMoistureMovementAac.setChecked(false);

        // NDT
        cbUltrasonicPulseVelocityNDT.setChecked(false);
        cbReboundHammerTestNDT.setChecked(false);

        // ConcCube
        cbCompressiveStrengthOfCube.setChecked(false);
        cbCastingPreparingCubesOfGivenMixCube.setChecked(false);
        cbFlexurerStrengthOfBeamCube.setChecked(false);
        cbCastingPreparingBeamCube.setChecked(false);

        // ConstWater
        cbSulphatesSO4CWater.setChecked(false);
        cbAlkalinityCWater.setChecked(false);
        cbPHValueCWater.setChecked(false);
        cbOrganicImpuritiesCWater.setChecked(false);
        cbInorganicImpuritiesCWater.setChecked(false);
        cbChlorideAsClCWater.setChecked(false);
        cbSuspendedMatterCWater.setChecked(false);
        cbTDSTotalDissolvedSolidsCWater.setChecked(false);

        // WasteWater
        cbChlorideAsClWasteWater.setChecked(false);
        cbPhValueWasteWater.setChecked(false);
        cbSulphatesSo4WasteWater.setChecked(false);
        cbTdsTotalDissolvedSolidsWasteWater.setChecked(false);
        cbTssTotalSuspendedSolidsWasteWater.setChecked(false);

        //Mix Design
        cbMixDesign.setChecked(false);
        cbWithCubeMixDesign.setChecked(false);
        cbWithFlexurerStrengthMixDesign.setChecked(false);
        cbWithAdmixtureMixDesign.setChecked(false);

        //Fly Ash
        cbFlyAsh.setChecked(false);
        cbSpecificGravityFlyAsh.setChecked(false);
        cbSoundnessFlyAsh.setChecked(false);
        cbCompressiveStrengthFlyAsh.setChecked(false);

        // Bitumen
        cbPenetrationValueBitumen.setChecked(false);
        cbSofteningPointBitumen.setChecked(false);
        cbDuctilityBitumen.setChecked(false);
        cbSpecificGravityBitumen.setChecked(false);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        isSubmitting = false;
        submitButton.setEnabled(true);
    }


//    private CheckBox getSpecificGravityAndWaterAbsorptionFine() {
//        return specificGravityAndWaterAbsorptionFine;
//    }

    private void setupPriceChangeListener(CheckBox checkBox, int price) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            totalPrice += isChecked ? price : -price;
            calculateTotalPrice();
        });
    }


    private void calculateTotalPrice() {
        String manualPriceStr = etFinalPrice.getText().toString().trim();
        if (!manualPriceStr.isEmpty()) {
            try {
                totalPrice = Integer.parseInt(manualPriceStr);  // Override totalPrice
            } catch (NumberFormatException e) {
                // Keep totalPrice unchanged if input is invalid
            }
        }
        tvTotalPrice.setText("Total Price: " + totalPrice + " /- Rs");
    }






    // Checkbox Listener to show/hide TextInputLayout
    private void setUpCheckboxListener(CheckBox checkBox, TextInputLayout textInputLayout) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                textInputLayout.setVisibility(View.VISIBLE);
            } else {
                textInputLayout.setVisibility(View.GONE);
            }
        });
    }
}