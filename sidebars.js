const sidebars = {
  tutorialSidebar: [
    'intro',
    {
      label: 'Getting Started',
      items: [
        'getting-started/quickstart',
        'getting-started/api-overview',
      ],
    },
    {
      label: 'Core Concepts',
      items: [
        'architecture',
        'models',
        'phases',
      ],
    },
    {
      label: 'Deep Dives',
      items: [
        'deep-dives/pdf-parsing',
        'deep-dives/rules-evaluation',
        'deep-dives/llm-integration',
        'deep-dives/database',
        'deep-dives/testing',
      ],
    },
  ],
};

module.exports = sidebars;
